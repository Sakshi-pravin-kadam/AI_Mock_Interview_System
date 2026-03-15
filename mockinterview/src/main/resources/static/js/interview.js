const sendBtn = document.getElementById("sendBtn");
const input = document.getElementById("userInput");
const chat = document.getElementById("chatArea");
const progress = document.querySelector(".progress");

// interview state
let questionCount = 1;
const totalQuestions = 5;

// session data
const sessionId = localStorage.getItem("sessionId");
const domain = localStorage.getItem("domain");
const topic = localStorage.getItem("topic");
const difficulty = localStorage.getItem("difficulty");


// SHOW AI MESSAGE
function addAIMessage(text){

    const msg = document.createElement("div");
    msg.className = "message ai";

    msg.innerHTML = `
        <div class="avatar">
            <i class="fa-solid fa-robot"></i>
        </div>
        <div class="bubble">${text}</div>
    `;

    chat.appendChild(msg);
    chat.scrollTop = chat.scrollHeight;

}


// SHOW USER MESSAGE
function addUserMessage(text){

    const msg = document.createElement("div");
    msg.className = "message user";

    msg.innerHTML = `<div class="bubble">${text}</div>`;

    chat.appendChild(msg);
    chat.scrollTop = chat.scrollHeight;

}


// LOAD FIRST QUESTION
async function loadFirstQuestion(){

    const response = await fetch("http://localhost:8080/api/start-interview",{

        method:"POST",

        headers:{
            "Content-Type":"application/json"
        },

        body:JSON.stringify({
            domain:domain,
            topic:topic,
            difficulty:difficulty
        })

    });

    const question = await response.text();

    addAIMessage(question);

}


// SEND ANSWER
sendBtn.onclick = async function(){

    const text = input.value.trim();

    if(text==="") return;

    addUserMessage(text);

    input.value = "";

    if(questionCount >= totalQuestions){

        addAIMessage("Interview completed. Generating feedback...");
        return;

    }

    const response = await fetch("http://localhost:8080/api/question",{

        method:"POST",

        headers:{
            "Content-Type":"application/json"
        },

        body:JSON.stringify({
            sessionId:sessionId,
            answer:text
        })

    });

    const nextQuestion = await response.text();

    questionCount++;

    progress.innerText = `Question ${questionCount} / ${totalQuestions}`;

    setTimeout(()=>{
        addAIMessage(nextQuestion);
    },800);

};


// TIMER
let time = 25 * 60;

function startTimer(){

    setInterval(()=>{

        let minutes = Math.floor(time/60);
        let seconds = time%60;

        document.getElementById("time").innerText =
            `${minutes}:${seconds<10?'0':''}${seconds}`;

        if(time > 0) time--;

    },1000);

}


// INIT
startTimer();
loadFirstQuestion();