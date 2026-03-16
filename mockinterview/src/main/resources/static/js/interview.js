const sendBtn = document.getElementById("sendBtn");
const input = document.getElementById("userInput");
const chat = document.getElementById("chatArea");
const progress = document.querySelector(".progress");

// interview state
let questionCount = 1;
const totalQuestions = 5;

// session data from previous page
const sessionId = localStorage.getItem("sessionId");
const domain = localStorage.getItem("domain");
const topic = localStorage.getItem("topic");
const difficulty = localStorage.getItem("difficulty");


// SHOW AI MESSAGE IN CHAT
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

    // auto scroll to latest message
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


// LOAD FIRST QUESTION WHEN INTERVIEW STARTS
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


// SEND USER ANSWER
sendBtn.onclick = async function(){

    const text = input.value.trim();

    // prevent empty answers
    if(text==="") return;

    // show user message
    addUserMessage(text);

    input.value = "";

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

    const data = await response.json();


    /*
        Backend response structure

        Normal response:
        {
            feedback: "...",
            nextQuestion: "...",
            completed: false
        }

        Final response:
        {
            completed: true,
            finalScore: 78,
            strengths: "...",
            weaknesses: "...",
            improvementPlan: "..."
        }
    */


    // SHOW AI FEEDBACK
    if(data.feedback){
        setTimeout(()=>{
            addAIMessage("Feedback:\n" + data.feedback);
        },500);
    }


    // IF INTERVIEW IS COMPLETED
    if(data.completed){

        setTimeout(()=>{

            addAIMessage(`
Interview Completed 🎉

Final Score: ${data.finalScore}/100

Strengths:
${data.strengths}

Weak Areas:
${data.weaknesses}

What To Study:
${data.improvementPlan}
            `);

        },1200);

        return;
    }


    // SHOW NEXT QUESTION
    if(data.nextQuestion){

        questionCount++;

        // update progress indicator
        progress.innerText = `Question ${questionCount} / ${totalQuestions}`;

        setTimeout(()=>{
            addAIMessage(data.nextQuestion);
        },1200);
    }

};


// TIMER (25 minutes interview)
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


// INITIALIZE INTERVIEW
startTimer();
loadFirstQuestion();