// CURSOR GLOW

const glow = document.querySelector(".cursor-glow")

document.addEventListener("mousemove",(e)=>{

    glow.style.left = e.clientX + "px"
    glow.style.top = e.clientY + "px"

})

/* MAGNETIC BUTTONS */

const magnets = document.querySelectorAll(".magnetic")

magnets.forEach(btn=>{

    btn.addEventListener("mousemove",(e)=>{

        const rect = btn.getBoundingClientRect()

        const x = e.clientX - rect.left - rect.width/2
        const y = e.clientY - rect.top - rect.height/2

        btn.style.transform = `translate(${x*0.2}px,${y*0.2}px)`

    })

    btn.addEventListener("mouseleave",()=>{

        btn.style.transform = "translate(0,0)"

    })

})

/* SCROLL REVEAL */

const reveals = document.querySelectorAll(".reveal")

window.addEventListener("scroll",()=>{

    reveals.forEach(el=>{

        const top = el.getBoundingClientRect().top
        const windowHeight = window.innerHeight

        if(top < windowHeight - 80){
            el.classList.add("active")
        }

    })

})