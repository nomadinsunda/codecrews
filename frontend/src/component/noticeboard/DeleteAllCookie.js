import axios from "axios";

export function deleteAllToken () {
    axios({
        method: "POST",
        mode: "cors",
        url: `/user/logout`,
    });
    localStorage.removeItem("userId");

    // window.location.href = 
    // "https://accounts.google.com/Logout?continue=https://appengine.google.com/_ah/logout?continue=http://localhost:3000/login";
}