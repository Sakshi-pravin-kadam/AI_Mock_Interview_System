const sendBtn = document.getElementById("sendBtn");
const input = document.getElementById("userInput");
const chat = document.getElementById("chatArea");
const progress = document.querySelector(".progress");

// interview state
let questionCount = 1;
let totalQuestions = 5;

// session data
let sessionId = localStorage.getItem("sessionId");

// USER INFO CHECK (must be logged in)
const userId = localStorage.getItem("userId");
const domain = localStorage.getItem("domain");
const topic = localStorage.getItem("topic");
const difficulty = localStorage.getItem("difficulty");

// Redirect if user not logged in
if (!userId) {
    alert("You must be logged in to start the interview.");
    window.location.href = "/login.html";
}

// Redirect if no active session
if (!sessionId) {
    alert("No active interview session found. Please start an interview.");
    window.location.href = "start_interview.html";
}

// SHOW AI MESSAGE
function addAIMessage(text){
    const msg = document.createElement("div");
    msg.className = "message ai";
    msg.innerHTML = `
        <div class="avatar">
            <i class="fa-solid fa-robot"></i>
        </div>
        <div class="bubble">${text.replace(/\n/g,"<br>")}</div>
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
    // If we already have sessionId (resume interview), skip creating new
    if (!sessionId) {
        const response = await fetch("http://localhost:8080/api/start-interview",{
            method:"POST",
            headers:{
                "Content-Type":"application/json"
            },
            body:JSON.stringify({
                userId: userId,
                domain: domain,
                topic: topic,
                difficulty: difficulty
            })
        });

        const data = await response.json();

        // store sessionId
        sessionId = data.sessionId;
        localStorage.setItem("sessionId", sessionId);

        totalQuestions = data.totalQuestions;

        addAIMessage(data.question);
    }

    progress.innerText = `Question ${questionCount} / ${totalQuestions}`;
}

// SEND USER ANSWER
sendBtn.onclick = async function(){
    const text = input.value.trim();
    if(text === "") return;

    addUserMessage(text);
    input.value = "";

    const response = await fetch("http://localhost:8080/api/question",{
        method:"POST",
        headers:{
            "Content-Type":"application/json"
        },
        body:JSON.stringify({
            sessionId: sessionId,
            answer: text
        })
    });

    const data = await response.json();

    // SHOW FEEDBACK
    if(data.feedback){
        setTimeout(()=> addAIMessage(`Feedback:\n${data.feedback}`), 500);
    }

    // IF INTERVIEW COMPLETED
    if(data.completed){
        setTimeout(()=>{
            addAIMessage(`
Interview Completed 🎉

Final Score: ${data.finalScore}

Best Topic: ${data.bestTopic}
Weak Topic: ${data.weakTopic}
            `);
        }, 1200);

        // clear sessionId after completion
        localStorage.removeItem("sessionId");
        return;
    }

    // SHOW NEXT QUESTION
    if(data.nextQuestion){
        questionCount++;
        progress.innerText = `Question ${questionCount} / ${totalQuestions}`;
        setTimeout(()=> addAIMessage(data.nextQuestion), 1200);
    }
};

// TIMER (25 minutes)
let time = 25 * 60;
function startTimer(){
    setInterval(()=>{
        let minutes = Math.floor(time/60);
        let seconds = time % 60;
        document.getElementById("time").innerText =
            `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
        if(time > 0) time--;
    },1000);
}

// INITIALIZE INTERVIEW
startTimer();
loadFirstQuestion();