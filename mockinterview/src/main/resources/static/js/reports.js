const token = localStorage.getItem("token");

async function loadReports() {

    try {

        const response = await fetch("http://localhost:8080/api/reports", {
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        const data = await response.json();

        // Summary
        document.getElementById("totalInterviews").textContent = data.totalInterviews;
        document.getElementById("averageScore").textContent = data.averageScore + "%";
        document.getElementById("bestTopic").textContent = data.bestTopic;
        document.getElementById("weakTopic").textContent = data.weakTopic;

        // Chart
        createChart(data.performance);

        // History
        loadHistory(data.recent);

    } catch (error) {
        console.error("Error loading reports:", error);
    }
}

function createChart(performanceData) {

    const ctx = document.getElementById("performanceChart");

    new Chart(ctx, {
        type: "line",
        data: {
            labels: performanceData.map((_, index) => "Interview " + (index + 1)),
            datasets: [{
                data: performanceData,
                borderColor: "#8b5cf6",
                backgroundColor: "rgba(139,92,246,0.2)",
                fill: true,
                tension: 0.4
            }]
        },
        options: {
            plugins: { legend: { display: false } },
            scales: { y: { beginAtZero: true, max: 100 } }
        }
    });
}

function loadHistory(recentData) {

    const container = document.getElementById("historyContainer");
    container.innerHTML = "";

    recentData.forEach(item => {

        const div = document.createElement("div");
        div.classList.add("history-item");

        div.innerHTML = `
            <div>
                <strong>${item.topic}</strong><br>
                ${item.domain.toUpperCase()}
            </div>
            <div>
                ${item.percentage}% <br>
                ${new Date(item.date).toLocaleDateString()}
            </div>
        `;

        container.appendChild(div);
    });
}

function logout() {
    localStorage.clear();
    window.location.href = "login.html";
}

loadReports();