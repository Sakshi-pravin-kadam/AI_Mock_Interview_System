const sendBtn = document.getElementById("sendBtn");
const input = document.getElementById("userInput");
const chat = document.getElementById("chatArea");
const progress = document.querySelector(".progress");

// interview state
let questionCount = 1;
let totalQuestions = localStorage.getItem("totalQuestions") || 5;

// session data
let sessionId = localStorage.getItem("sessionId");

// USER INFO CHECK
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
    alert("No active interview session found.");
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

// ✅ LOAD FIRST QUESTION (FIXED)
async function loadFirstQuestion(){

    try{
        addAIMessage("Loading question...");

        const response = await fetch("http://localhost:8080/api/first-question",{
            method:"POST",
            headers:{
                "Content-Type":"application/json"
            },
            body: JSON.stringify({
                sessionId: sessionId
            })
        });

        if(!response.ok){
            throw new Error("Failed to fetch question");
        }

        const data = await response.json();

        // remove loading message
        chat.lastChild.remove();

        addAIMessage(data.question);

    } catch(error){
        console.error(error);
        addAIMessage("❌ Error loading question");
    }

    progress.innerText = `Question ${questionCount} / ${totalQuestions}`;
}

// SEND USER ANSWER
sendBtn.onclick = async function(){
    const text = input.value.trim();
    if(text === "") return;

    addUserMessage(text);
    input.value = "";

    try{
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

        // ✅ INTERVIEW COMPLETED
        if(data.completed){
            setTimeout(()=>{
                addAIMessage(`
Interview Completed 🎉

Final Score: ${data.finalScore} / ${data.maxScore}

Best Topic: ${data.bestTopic}
Weak Topic: ${data.weakTopic}
                `);
            }, 1200);

            localStorage.removeItem("sessionId");
            return;
        }

        // NEXT QUESTION
        if(data.nextQuestion){
            questionCount++;
            progress.innerText = `Question ${questionCount} / ${totalQuestions}`;
            setTimeout(()=> addAIMessage(data.nextQuestion), 1200);
        }

    } catch(error){
        console.error(error);
        addAIMessage("❌ Error submitting answer");
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

// INITIALIZE
startTimer();
loadFirstQuestion();