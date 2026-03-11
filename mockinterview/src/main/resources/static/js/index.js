// HAMBURGER MENU

const hamburger = document.getElementById("hamburger");
const navMenu = document.getElementById("navMenu");

hamburger.addEventListener("click", () => {
    navMenu.classList.toggle("active");
});



/* PARTICLES */

particlesJS("particles-js", {

    particles: {
        number: { value: 60 },
        color: { value: "#a855f7" },
        shape: { type: "circle" },
        opacity: { value: 0.5 },
        size: { value: 3 },
        line_linked: {
            enable: true,
            distance: 150,
            color: "#6366f1",
            opacity: 0.4,
            width: 1
        },
        move: {
            enable: true,
            speed: 2
        }
    },

    interactivity: {
        events: {
            onhover: { enable: true, mode: "repulse" }
        }
    },

    retina_detect: true

});

/* SCROLL REVEAL */

function reveal(){

    const reveals = document.querySelectorAll(".reveal");

    reveals.forEach(element => {

        const windowHeight = window.innerHeight;
        const elementTop = element.getBoundingClientRect().top;
        const revealPoint = 120;

        if(elementTop < windowHeight - revealPoint){
            element.classList.add("active");
        }

    });

}

window.addEventListener("scroll", reveal);

/* MAGNETIC BUTTONS */

const magneticButtons = document.querySelectorAll(
    ".btn-primary, .btn-secondary, .cta-btn, .btn-register"
);

magneticButtons.forEach(button => {

    button.addEventListener("mousemove", function(e){

        const rect = button.getBoundingClientRect();
        const x = e.clientX - rect.left - rect.width / 2;
        const y = e.clientY - rect.top - rect.height / 2;

        button.style.transform = `translate(${x*0.2}px, ${y*0.2}px) scale(1.05)`;

    });

    button.addEventListener("mouseleave", function(){

        button.style.transform = "translate(0,0) scale(1)";

    });

});

/* CURSOR GLOW FOLLOW */

const glow = document.querySelector(".cursor-glow");

document.addEventListener("mousemove", (e)=>{

    glow.style.left = e.clientX + "px";
    glow.style.top = e.clientY + "px";

});

const observer = new IntersectionObserver(entries => {

    entries.forEach(entry => {

        if(entry.isIntersecting){
            entry.target.classList.add("active");
        }

    });

},{threshold:0.2});

document.querySelectorAll(".reveal").forEach(el=>{
    observer.observe(el);
});