

import { Fragment, useEffect, useState, useMemo } from "react";
import { Map } from "./kakao-maps/esm/components/Map.js";
import { MapMarker } from "./kakao-maps/esm/components/MapMarker.js";

const { kakao } = window;

const Maps = (props) => {
  const [locate, setLocate] = useState("지도를 눌러주세요.");
  const [position, setPosition] = useState(); // 마우스 클릭 또는 현재 위치용
  const [search, setSearch] = useState("");
  const [info, setInfo] = useState(); // 인포윈도우용
  const [markers, setMarkers] = useState([]); // 검색 결과용
  const [map, setMap] = useState();

  // 서비스 객체들을 메모이제이션하여 불필요한 재생성 방지
  const geocoder = useMemo(() => new kakao.maps.services.Geocoder(), []);
  const ps = useMemo(() => new kakao.maps.services.Places(), []);

  const onChangeSearch = (e) => setSearch(e.target.value);
  const onChangeDetainAddress = (e) => props.detailAddressData(e.target.value);

  // 좌표를 주소로 변환하는 핵심 함수
  const findAddress = (lat, lng) => {
    let coord = new kakao.maps.LatLng(lat, lng);
    
    // 부모 컴포넌트로 좌표 전달
    if (props.locateData) {
      props.locateData({ locateX: lng, locateY: lat });
    }

    geocoder.coord2Address(lng, lat, (result, status) => {
      if (status === kakao.maps.services.Status.OK) {
        const addressName = result[0].address ? result[0].address.address_name : "알 수 없음.";
        setLocate(addressName);
      }
    });
  };

  // 주소가 바뀔 때마다 부모 컴포넌트에 알림
  useEffect(() => {
    if (props.addressData) props.addressData(locate);
  }, [locate]);

  // 키워드 검색 함수
  const searchAddress = (e) => {
    if (e.key === 'Enter' && map) {
      ps.keywordSearch(search, (data, status) => {
        if (status === kakao.maps.services.Status.OK) {
          const bounds = new kakao.maps.LatLngBounds();
          let newMarkers = [];

          for (let i = 0; i < data.length; i++) {
            newMarkers.push({
              position: { lat: data[i].y, lng: data[i].x },
              content: data[i].place_name,
            });
            bounds.extend(new kakao.maps.LatLng(data[i].y, data[i].x));
          }
          setMarkers(newMarkers);
          map.setBounds(bounds); // 검색 결과에 맞춰 지도 범위 재설정
        }
      });
    }
  };

  // 현재 위치 가져오기
  const currentLocation = () => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (pos) => {
          const lat = pos.coords.latitude;
          const lng = pos.coords.longitude;
          const locPosition = { lat, lng };

          setPosition(locPosition);
          findAddress(lat, lng); // 현재 위치의 주소도 가져오기
          map.setCenter(new kakao.maps.LatLng(lat, lng));
        },
        () => alert("위치 정보를 가져올 수 없습니다.")
      );
    }
  };

  return (
    <Fragment>
      <input 
        className="meetingAddressSearch" 
        placeholder="검색할 주소를 입력 후 엔터를 눌러주세요." 
        onChange={onChangeSearch} 
        onKeyDown={searchAddress} 
      />
      
      <span className="meetingAddress">
        {props.address === undefined ? `주소 : ${locate}` : `주소 : ${props.address}`}
      </span>
      
      <button className="currentButton" onClick={currentLocation}>현재 위치</button>

      <Map
        className="IMG-newMeeting"
        center={{ lat: 33.450701, lng: 126.570667 }}
        style={{ width: "100%", height: "450px" }}
        onCreate={setMap}
        level={3}
        onClick={(_t, mouseEvent) => {
          const lat = mouseEvent.latLng.getLat();
          const lng = mouseEvent.latLng.getLng();
          setPosition({ lat, lng });
          findAddress(lat, lng);
          setInfo(null); // 클릭 시 기존 마커 인포창 닫기
        }}
      >
        {/* 직접 클릭하거나 현재 위치로 표시되는 마커 */}
        {position && <MapMarker position={position} />}

        {/* 검색 결과 마커들 */}
        {markers.map((marker, index) => (
          <MapMarker
            key={`marker-${index}-${marker.position.lat}`}
            position={marker.position}
            onClick={() => {
              setInfo(marker);
              setPosition(marker.position); // 마커 클릭 시 해당 위치를 선택 위치로 지정
              findAddress(marker.position.lat, marker.position.lng);
            }}
          >
            {info && info.content === marker.content && (
              <div style={{ padding: "5px", color: "#000", minWidth: "150px" }}>
                {marker.content}
              </div>
            )}
          </MapMarker>
        ))}
      </Map>

      <input 
        className="meetingAddressInput" 
        maxLength={20} 
        placeholder="상세 주소를 입력해주세요." 
        defaultValue={props.detailAddress} 
        onChange={onChangeDetainAddress} 
      />
    </Fragment>
  );
};

export default Maps;

// import { Fragment, useEffect, useState } from "react";
// import { Map } from "./kakao-maps/esm/components/Map.js";
// import { MapMarker } from "./kakao-maps/esm/components/MapMarker.js";

// const { kakao } = window;

// const Maps = (props) => {
//   const [locate, setLocate] = useState("지도를 눌러주세요.");
//   const [position, setPosition] = useState();
//   const [search , setSearch] = useState();

//   const [info, setInfo] = useState()
//   const [markers, setMarkers] = useState([])
//   const [map, setMap] = useState()

//   const onChangeSearch = (e) => {
//     setSearch(e.target.value);
//   }

//   const onChangeDetainAddress = (e) => {
//     props.detailAddressData(e.target.value);
//   }

//   var geocoder = new kakao.maps.services.Geocoder(); // 좌표로 주소 찾기
//   const ps = new kakao.maps.services.Places(); // 주소로 자표찾기

//   const findAddress = (lat , lng) => {
//   let coord = new kakao.maps.LatLng(lat, lng);
//   props.locateData({locateX : coord.getLng() , locateY : coord.getLat()});

//   geocoder.coord2Address(coord.getLng(), coord.getLat(), function(result, status) {
//   if (status === kakao.maps.services.Status.OK) {
//       setLocate(!! result[0].address ? result[0].address.address_name : "알 수 없음.");
//     }
//   });
//   }


//   useEffect(() => {
//     props.addressData(locate);
//   } , [locate]);

//   const searchAddress = (e) => {
//     if(e.key == 'Enter') {
//       ps.keywordSearch(search , (data, status, _pagination) => {
//         if (status === kakao.maps.services.Status.OK) {
//           // 검색된 장소 위치를 기준으로 지도 범위를 재설정하기위해
//           // LatLngBounds 객체에 좌표를 추가합니다
//           const bounds = new kakao.maps.LatLngBounds()
//           let markers = []
    
//           for (var i = 0; i < data.length; i++) {
//             // @ts-ignore
//             markers.push({
//               position: {
//                 lat: data[i].y,
//                 lng: data[i].x,
//               },
//               content: data[i].place_name,
//             })
//             // @ts-ignore
//             bounds.extend(new kakao.maps.LatLng(data[i].y, data[i].x))
//           }
//           setMarkers(markers)
    
//           // 검색된 장소 위치를 기준으로 지도 범위를 재설정합니다
//           map.setBounds(bounds)
//         }
//       })
//     }
//   }

//   const currentLocation = () => {
//     // HTML5의 geolocation으로 사용할 수 있는지 확인합니다
//     if (navigator.geolocation) {
    
//       // GeoLocation을 이용해서 접속 위치를 얻어옵니다
//       navigator.geolocation.getCurrentPosition(function(position) {
          
//           var lat = position.coords.latitude, // 위도
//               lon = position.coords.longitude; // 경도
          
//           var locPosition = new kakao.maps.LatLng(lat, lon), // 마커가 표시될 위치를 geolocation으로 얻어온 좌표로 생성합니다
//               message = '<div style="padding:5px;">여기에 계신가요?!</div>'; // 인포윈도우에 표시될 내용입니다
          
//           // 마커와 인포윈도우를 표시합니다
//           displayMarker(locPosition, message);
              
//         });
//      } else { // HTML5의 GeoLocation을 사용할 수 없을때 마커 표시 위치와 인포윈도우 내용을 설정합니다
    
//       var locPosition = new kakao.maps.LatLng(33.450701, 126.570667),    
//           message = 'geolocation을 사용할수 없어요..'
          
//       displayMarker(locPosition, message);
//       }
//     }

//     function displayMarker(locPosition, message) {

//       // 마커를 생성합니다
//       var marker = new kakao.maps.Marker({  
//           map: map, 
//           position: locPosition
//       }); 
      
//       var iwContent = message, // 인포윈도우에 표시할 내용
//           iwRemoveable = true;
  
//       // 인포윈도우를 생성합니다
//       var infowindow = new kakao.maps.InfoWindow({
//           content : iwContent,
//           removable : iwRemoveable
//       });
      
//       // 인포윈도우를 마커위에 표시합니다 
//       infowindow.open(map, marker);
      
//       // 지도 중심좌표를 접속위치로 변경합니다
//       map.setCenter(locPosition);      
//   }

//   useEffect(() => {
    
//   } , []);

//   return (
//     <Fragment>
//       <input className="meetingAddressSearch" placeholder="검색할 주소를 입력 후 엔터를 눌러주세요." onChange={onChangeSearch} onKeyDown={searchAddress}/>
//       {props.address == undefined ? <span className="meetingAddress">{"주소 : 지도를 눌러주세요."}</span> : <span className="meetingAddress">{"주소 : " + props.address}</span>}
//       <button className="currentButton" onClick={currentLocation}>현재 위치</button>
//     <Map // 지도를 표시할 Container
//       className="IMG-newMeeting"
//           center={{
//             // 지도의 중심좌표
//             lat: 33.450701,
//             lng: 126.570667,
//           }}
//           style={{
//             width: "100%",
//             height: "450px",
//           }}
//           onCreate={setMap}
//           level={3} // 지도의 확대 레벨
//           onClick={(_t, mouseEvent) => {
//             findAddress(mouseEvent.latLng.getLat() , mouseEvent.latLng.getLng());
//             setPosition({
//               lat: mouseEvent.latLng.getLat(),
//               lng: mouseEvent.latLng.getLng(),
//             })
//           }}
//         >
//           {position && <MapMarker position={position} />}
//           {markers.map((marker) => (
//         <MapMarker
//           key={`marker-${marker.content}-${marker.position.lat},${marker.position.lng}`}
//           position={marker.position}
//           onClick={() => setInfo(marker)}
//         >
//           {info &&info.content === marker.content && (
//             <div style={{color:"#000"}}>{marker.content}</div>
//           )}
//         </MapMarker>
//       ))}
//         </Map>
//         <input className="meetingAddressInput" maxLength={20} placeholder="상세 주소를 입력해주세요." defaultValue={props.detailAddress} onChange={onChangeDetainAddress}/>
//       </Fragment>
//   )
// }



// export default Maps;