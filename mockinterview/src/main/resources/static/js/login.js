const canvas = document.getElementById("bg");
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

        if (this.x > canvas.width || this.x < 0) {
            this.speedX *= -1;
        }

        if (this.y > canvas.height || this.y < 0) {
            this.speedY *= -1;
        }
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

    for (let i = 0; i < particlesArray.length; i++) {
        particlesArray[i].update();
        particlesArray[i].draw();
    }

    connect();
    requestAnimationFrame(animate);
}

init();
animate();

function validateLogin(){

    let email = document.getElementById("email").value.trim();
    let password = document.getElementById("password").value;

    if(email === ""){
        alert("Please enter email");
        return;
    }

    if(password === ""){
        alert("Please enter password");
        return;
    }

    console.log("Sending login request...");

    fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            email: email,
            password: password
        })
    })

        .then(response => response.text())

        .then(data => {

            console.log("Server response:", data);

            if(data === "Login successful"){

                alert("Login successful");
                localStorage.setItem("username", email);

                window.location.href = "/Home.html";

            }else{

                alert(data);

            }

        })

        .catch(error => {

            console.error("Login error:", error);
            alert("Server error");

        });

}

document.getElementById("loginForm").addEventListener("submit", function(e){

    e.preventDefault(); // stop page reload
    validateLogin();    // call login function

});