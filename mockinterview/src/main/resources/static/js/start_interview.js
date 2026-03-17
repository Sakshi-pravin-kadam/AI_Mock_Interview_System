// Selected values
let selectedDomain = "";
let selectedDifficulty = "Beginner";

// DOMAIN SELECTION
document.querySelectorAll(".domain").forEach(card => {
    card.addEventListener("click", function(){
        document.querySelectorAll(".domain").forEach(d => d.classList.remove("active"));
        this.classList.add("active");
        selectedDomain = this.dataset.domain;
        loadTopics(selectedDomain);
    });
});

// LOAD TOPICS BASED ON DOMAIN
function loadTopics(domain){
    const topicSelect = document.getElementById("topic");
    topicSelect.innerHTML = "<option value=''>Select topic</option>";

    const topics = {
        java: ["Core Java","Java OOP","Java Collections","Multithreading","Spring Boot"],
        mern: ["React","NodeJS","MongoDB","ExpressJS","REST API"],
        python: ["Python Basics","Data Structures","Machine Learning","Flask","Django"],
        data: ["SQL","Power BI","Data Visualization","Statistics","ETL"]
    };

    topics[domain].forEach(topic => {
        const option = document.createElement("option");
        option.value = topic;
        option.textContent = topic;
        topicSelect.appendChild(option);
    });
}

// DIFFICULTY SELECTION
document.querySelectorAll(".level").forEach(level => {
    level.addEventListener("click", function(){
        document.querySelectorAll(".level").forEach(l => l.classList.remove("active"));
        this.classList.add("active");
        selectedDifficulty = this.dataset.level;
    });
});

// START INTERVIEW
let isStarting = false; // ✅ prevent multiple clicks

async function startInterview(){

    if(isStarting) return; // block double click
    isStarting = true;

    const btn = document.getElementById("startBtn");
    btn.disabled = true;
    btn.innerText = "Starting...";

    const topic = document.getElementById("topic").value;

    if(!selectedDomain){
        alert("Please select a domain");
        resetButton(btn);
        return;
    }

    if(!topic){
        alert("Please select a topic");
        resetButton(btn);
        return;
    }

    const userId = localStorage.getItem("userId");
    if(!userId){
        alert("You must be logged in to start the interview.");
        window.location.href = "/login.html";
        return;
    }

    try{
        const response = await fetch("http://localhost:8080/api/start-interview", {
            method: "POST",
            headers:{
                "Content-Type":"application/json"
            },
            body: JSON.stringify({
                userId: userId,
                domain: selectedDomain,
                topic: topic,
                difficulty: selectedDifficulty
            })
        });

        const data = await response.json();

        console.log("START RESPONSE:", data); // ✅ debug

        // ✅ STORE EVERYTHING IMPORTANT
        localStorage.setItem("sessionId", data.sessionId);
        localStorage.setItem("domain", selectedDomain);
        localStorage.setItem("topic", topic);
        localStorage.setItem("difficulty", selectedDifficulty);

        // ⭐ IMPORTANT FIX (YOU MISSED THIS)
        localStorage.setItem("firstQuestion", data.question);
        localStorage.setItem("totalQuestions", data.totalQuestions);

        // redirect
        window.location.href = "interview.html";

    } catch(error){
        console.error(error);
        alert("Failed to start interview");
        resetButton(btn);
    }
}

// helper function
function resetButton(btn){
    isStarting = false;
    btn.disabled = false;
    btn.innerText = "Begin Interview";
}