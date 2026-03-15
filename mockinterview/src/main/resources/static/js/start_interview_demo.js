const sendBtn=document.getElementById("sendBtn");
const input=document.getElementById("userInput");
const chat=document.getElementById("chatArea");

sendBtn.onclick=function(){

    const text=input.value.trim();

    if(text==="") return;

    const msg=document.createElement("div");

    msg.className="message user";

    msg.innerHTML=`<div class="bubble">${text}</div>`;

    chat.appendChild(msg);

    input.value="";

    chat.scrollTop=chat.scrollHeight;

};