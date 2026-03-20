/* ============================= */
/* FETCH DASHBOARD DATA FROM API */
/* ============================= */

const userId = localStorage.getItem("userId");

if (userId) {

    fetch(`http://localhost:8080/api/dashboard/${userId}`)
        .then(res => {
            if (!res.ok) {
                throw new Error("Failed to fetch dashboard data");
            }
            return res.json();
        })
        .then(data => {

            /* ----------- Cards ----------- */

            document.getElementById("overallScore").textContent =
                (data.averageScore ?? 0) + "%";

            document.getElementById("totalInterviews").textContent =
                data.totalInterviews ?? 0;

            document.getElementById("bestTopic").textContent =
                data.bestTopic ?? "-";

            document.getElementById("weakTopic").textContent =
                data.weakTopic ?? "-";


            /* ----------- Chart ----------- */

            const ctx = document.getElementById("performanceChart");

            if (ctx) {

                const performanceData = data.performance ?? [];

                new Chart(ctx, {
                    type: "line",
                    data: {
                        labels: performanceData.map((_, index) => `Attempt ${index + 1}`),
                        datasets: [{
                            data: performanceData,
                            borderColor: "#8b5cf6",
                            backgroundColor: "rgba(139,92,246,0.2)",
                            fill: true,
                            tension: .4
                        }]
                    },
                    options: {
                        plugins: { legend: { display: false } },
                        scales: { y: { beginAtZero: true, max: 100 } }
                    }
                });
            }


            /* ----------- Recent Interviews ----------- */

            const recentContainer = document.getElementById("recentContainer");
            recentContainer.innerHTML = "";

            if (data.recent && data.recent.length > 0) {

                data.recent.forEach(interview => {

                    const div = document.createElement("div");
                    div.classList.add("interview-card");

                    div.innerHTML = `
                        ${interview.topic ?? "Interview"}
                        <span>${interview.percentage ?? 0}%</span>
                    `;

                    recentContainer.appendChild(div);
                });

            } else {

                recentContainer.innerHTML =
                    `<div class="interview-card">No interviews yet <span>0%</span></div>`;
            }

        })
        .catch(err => {
            console.error("Dashboard Error:", err);
        });

}


/* ============================= */
/* Reveal animation */
/* ============================= */

function reveal(){

    const items=document.querySelectorAll(".reveal");

    items.forEach(el=>{

        const top=el.getBoundingClientRect().top;
        const windowHeight=window.innerHeight;

        if(top < windowHeight - 50){
            el.classList.add("active");
        }

    });

}

/* run reveal when page loads */
window.addEventListener("load", reveal);

/* run reveal when scrolling */
window.addEventListener("scroll", reveal);



/* ============================= */
/* Sidebar toggle */
/* ============================= */

const toggle=document.getElementById("toggleSidebar");
const sidebar=document.getElementById("sidebar");

if(toggle){
    toggle.onclick=()=>{
        sidebar.classList.toggle("collapse");
    };
}


/* ============================= */
/* Username Load */
/* ============================= */

const username = localStorage.getItem("username");

if(username){
    document.getElementById("username").textContent = username;
}


/* ============================= */
/* Logout */
/* ============================= */

function logout(){

    localStorage.clear();

    window.location.href="login.html";

}