let domain=""
let difficulty="Intermediate"


/* domain selection */

document.querySelectorAll(".domain").forEach(card=>{

    card.onclick=function(){

        document.querySelectorAll(".domain").forEach(c=>c.classList.remove("active"))

        this.classList.add("active")

        domain=this.dataset.domain

    }

})


/* difficulty */

document.querySelectorAll(".level").forEach(card=>{

    card.onclick=function(){

        document.querySelectorAll(".level").forEach(c=>c.classList.remove("active"))

        this.classList.add("active")

        difficulty=this.dataset.level

    }

})


/* start interview */

function startInterview(){

    let topic=document.getElementById("topic").value

    if(!domain || !topic){

        alert("Please select domain and topic")

        return

    }

    localStorage.setItem("domain",domain)
    localStorage.setItem("topic",topic)
    localStorage.setItem("difficulty",difficulty)

    window.location.href="interview.html"

}

const username = localStorage.getItem("username");

if(username){
    document.getElementById("username").textContent = username;
}
function logout(){

    localStorage.clear();

    window.location.href="login.html";

}