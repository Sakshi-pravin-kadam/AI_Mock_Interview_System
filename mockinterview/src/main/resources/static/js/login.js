// ================================
// CANVAS BACKGROUND (UNCHANGED)
// ================================
const canvas = document.getElementById("bg");
if (canvas) {
    const ctx = canvas.getContext("2d");

    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;

    let particlesArray = [];
    const numberOfParticles = 80;

    class Particle {
        constructor() {
            this.x = Math.random() * canvas.width;
            this.y = Math.random() * canvas.height;
            this.size = 2;
            this.speedX = (Math.random() * 1) - 0.5;
            this.speedY = (Math.random() * 1) - 0.5;
        }

        update() {
            this.x += this.speedX;
            this.y += this.speedY;

            if (this.x > canvas.width || this.x < 0) this.speedX *= -1;
            if (this.y > canvas.height || this.y < 0) this.speedY *= -1;
        }

        draw() {
            ctx.fillStyle = "#19e0b5";
            ctx.beginPath();
            ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2);
            ctx.fill();
        }
    }

    function init() {
        for (let i = 0; i < numberOfParticles; i++) {
            particlesArray.push(new Particle());
        }
    }

    function connect() {
        for (let a = 0; a < particlesArray.length; a++) {
            for (let b = a; b < particlesArray.length; b++) {
                let dx = particlesArray[a].x - particlesArray[b].x;
                let dy = particlesArray[a].y - particlesArray[b].y;
                let distance = dx * dx + dy * dy;

                if (distance < 12000) {
                    ctx.strokeStyle = "rgba(25,224,181,0.15)";
                    ctx.lineWidth = 1;
                    ctx.beginPath();
                    ctx.moveTo(particlesArray[a].x, particlesArray[a].y);
                    ctx.lineTo(particlesArray[b].x, particlesArray[b].y);
                    ctx.stroke();
                }
            }
        }
    }

    function animate() {
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        particlesArray.forEach(p => {
            p.update();
            p.draw();
        });
        connect();
        requestAnimationFrame(animate);
    }

    init();
    animate();
}

// ================================
// LOGIN LOGIC
// ================================

// Utility: Safe JSON parser
async function parseResponse(response) {
    const text = await response.text();
    try {
        return JSON.parse(text);
    } catch {
        return { message: text };
    }
}

// Utility: Store user session safely
function storeUserSession(data) {
    if (!data) return;

    if (data.userId) {
        localStorage.setItem("userId", String(data.userId));
    }

    if (data.name) {
        localStorage.setItem("username", data.name);
    }

    if (data.token) {
        localStorage.setItem("token", data.token);
    }
}

// Validate login inputs
function validateLoginInputs(email, password) {
    if (!email) {
        alert("Please enter email");
        return false;
    }

    if (!password) {
        alert("Please enter password");
        return false;
    }

    return true;
}

// Main login function
async function handleLogin(event) {
    event.preventDefault();

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;

    if (!validateLoginInputs(email, password)) return;

    console.log("Sending login request...");

    try {
        const response = await fetch("http://localhost:8080/api/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ email, password })
        });

        const data = await parseResponse(response);

        console.log("Server response:", data);

        // Handle HTTP errors
        if (!response.ok) {
            throw new Error(data.message || "Login failed");
        }

        // Success case
        if (
            data.message === "Login successful" ||
            data.message === "Login Successful" ||
            response.status === 200
        ) {
            alert("Login successful");

            storeUserSession(data);

            // Final safety check
            if (!localStorage.getItem("userId")) {
                console.warn("userId missing from response!");
            }

            window.location.href = "/Home.html";
        } else {
            alert(data.message || "Invalid credentials");
        }

    } catch (error) {
        console.error("Login error:", error);
        alert(error.message || "Server error. Please try again.");
    }
}

// Attach event listener safely
const loginForm = document.getElementById("loginForm");
if (loginForm) {
    loginForm.addEventListener("submit", handleLogin);
}