/* eslint-disable react-hooks/exhaustive-deps */
import React, { useEffect, useRef, useState, useCallback } from 'react';
import axios from "axios";
import { Client } from '@stomp/stompjs'; // stompjs 대신 @stomp/stompjs 사용
import SockJS from 'sockjs-client';

import '../../_style/meeting/chat.css';
import '../../_style/meeting/chatData.css';
import exit from '../../_image/exit.png';
import defaultALT from '../../_image/defaultALT.png';

const Chat = ({ dropDownSet }) => {
  const [chatRoom, setChatRoom] = useState([]);
  const [meetingId, setMeetingId] = useState(0);
  const [isRoomList, setIsRoomList] = useState(true);
  const [chatData, setChatData] = useState([]);
  const [userInfo, setUserInfo] = useState({ userId: "", profileImage: "" });
  const [inputData, setInputData] = useState("");

  const stompClient = useRef(null);
  const messagesEndRef = useRef(null);

  const scrollToBottom = useCallback(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, []);

  useEffect(() => {
    scrollToBottom();
  }, [chatData, scrollToBottom]);

  // 1. 초기 데이터 로딩
  useEffect(() => {
    const fetchInitialData = async () => {
      try {
        const [roomRes, userRes] = await Promise.all([
          axios.get(`/chat/participants`),
          axios.get(`/users`)
        ]);
        setChatRoom(roomRes.data);
        const { id, profileImage } = userRes.data.data;
        setUserInfo({ userId: id, profileImage });
      } catch (err) {
        console.error("Data Loading Error:", err);
      }
    };
    fetchInitialData();
  }, []);

  // 2. STOMP 연결 및 채팅방 입장
  const enterChatRoom = async (mId) => {
    setMeetingId(mId);
    setIsRoomList(false);
    setChatData([]);

    // 기존 내역 로드
    try {
      const res = await axios.get(`/chat/${mId}`);
      setChatData(res.data);
    } catch (err) { console.error(err); }

    // 클라이언트 설정
    const client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      reconnectDelay: 5000,
      debug: (str) => console.log(str), // 연결 과정을 콘솔에서 확인 가능
      onConnect: () => {
        console.log("STOMP Connected!");

        // 구독 경로 (서버의 SimpMessagingTemplate 경로와 일치)
        client.subscribe(`/topic/chat.${mId}`, (message) => {
          const received = JSON.parse(message.body);
          setChatData((prev) => [...prev, received]);
        });

        // 입장 메시지 발행 (서버의 @MessageMapping 경로와 일치)
        client.publish({
          destination: `/app/chat.enter.${mId}`,
          body: JSON.stringify({
            sender: userInfo.userId,
            meetingId: mId,
            messageType: "ENTER"
          })
        });
      },
      onStompError: (frame) => {
        console.error('STOMP Error:', frame.headers['message']);
      }
    });

    client.activate();
    stompClient.current = client;
  };

  // 3. 메시지 전송 (Enter 입력 시)
  const onPushEnter = (e) => {
    if (e.key === 'Enter') {
      if (!inputData.trim()) return;

      if (stompClient.current && stompClient.current.connected) {
        // 서버의 @MessageMapping("chat.send.{meetingId}")와 일치
        stompClient.current.publish({
          destination: `/app/chat.send.${meetingId}`,
          body: JSON.stringify({
            sender: userInfo.userId,
            message: inputData,
            senderImage: userInfo.profileImage,
            messageType: "SEND",
          })
        });
        setInputData("");
      }
    }
  };

  // 클린업: 방을 나가거나 닫을 때 연결 해제
  const leaveChat = () => {
    if (stompClient.current) {
      stompClient.current.deactivate();
      stompClient.current = null;
    }
    setIsRoomList(true);
  };

  return (
    <div className="element-chat">
      <div className="div-chat">
        <div className="text-wrapper-4-chat">
          {!isRoomList && <span onClick={leaveChat} style={{cursor:'pointer', marginRight:'10px'}}>&lt;</span>}
          {isRoomList ? "모임 채팅" : "채팅 내용"}
        </div>
        <div className="chatContentArea">
          {isRoomList ? (
            chatRoom.map(room => (
              <div className="chatContent" key={room.chatId} onClick={() => enterChatRoom(room.meetingId)}>
                <img className="ellipse-chat" alt="img" src={room.meetingImage || defaultALT} />
                <div className="text-wrapper-chat">{room.meetingTitle}</div>
                <div className="text-wrapper-chat-last">개설일: {room.createDate}</div>
              </div>
            ))
          ) : (
            <div className="element-chatData">
              <div className="div-chatData">
                {chatData.map((msg, idx) => (
                  <div className="overlap-group-chatData" key={msg.chatId || idx}>
                    <img className="mask-group-chatData" alt="img" src={msg.senderImage || defaultALT} />
                    <div className="text-wrapper-7-chatData">{msg.sender}</div>
                    <span className="rectangle-chatData">{msg.message}</span>
                    <div className="text-wrapper-4-chatData">{msg.sendTime}</div>
                  </div>
                ))}
                <div ref={messagesEndRef} />
              </div>
              <div className="rectangle-3-chatData">
                <input 
                  className="chattingInput" 
                  value={inputData}
                  onChange={(e) => setInputData(e.target.value)} 
                  onKeyDown={onPushEnter}
                  placeholder="메시지를 입력하세요..."
                />
              </div>
            </div>
          )}
        </div>
        <img className="light-s-chat" alt="Exit" src={exit} onClick={dropDownSet} />
      </div>
    </div>
  );
};

export default Chat;