// Selected values
let selectedDomain = "";
let selectedDifficulty = "Beginner";

// DOMAIN SELECTION
document.querySelectorAll(".domain").forEach(card => {

    card.addEventListener("click", function(){

        // remove active from all
        document.querySelectorAll(".domain")
            .forEach(d => d.classList.remove("active"));

        // add active to clicked
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

        java:[
            "Core Java",
            "Java OOP",
            "Java Collections",
            "Multithreading",
            "Spring Boot"
        ],

        mern:[
            "React",
            "NodeJS",
            "MongoDB",
            "ExpressJS",
            "REST API"
        ],

        python:[
            "Python Basics",
            "Data Structures",
            "Machine Learning",
            "Flask",
            "Django"
        ],

        data:[
            "SQL",
            "Power BI",
            "Data Visualization",
            "Statistics",
            "ETL"
        ]

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

        document.querySelectorAll(".level")
            .forEach(l => l.classList.remove("active"));

        this.classList.add("active");

        selectedDifficulty = this.dataset.level;

    });

});


// START INTERVIEW
async function startInterview(){

    const topic = document.getElementById("topic").value;

    if(!selectedDomain){
        alert("Please select a domain");
        return;
    }

    if(!topic){
        alert("Please select a topic");
        return;
    }

    try{

        const response = await fetch("http://localhost:8080/api/start-interview", {

            method: "POST",

            headers:{
                "Content-Type":"application/json"
            },

            body: JSON.stringify({
                domain: selectedDomain,
                topic: topic,
                difficulty: selectedDifficulty
            })

        });

        const sessionId = await response.text();

        // store interview data
        localStorage.setItem("sessionId", sessionId);
        localStorage.setItem("domain", selectedDomain);
        localStorage.setItem("topic", topic);
        localStorage.setItem("difficulty", selectedDifficulty);

        // redirect to interview page
        window.location.href = "interview.html";

    }
    catch(error){

        console.error(error);
        alert("Failed to start interview");

    }

}