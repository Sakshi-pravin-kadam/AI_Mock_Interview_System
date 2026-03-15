/* Chart */

const ctx=document.getElementById("performanceChart");

if(ctx){
    new Chart(ctx,{
        type:"line",
        data:{
            labels:["Jan","Feb","Mar","Apr","May","Jun"],
            datasets:[{
                data:[65,72,68,78,82,86],
                borderColor:"#8b5cf6",
                backgroundColor:"rgba(139,92,246,0.2)",
                fill:true,
                tension:.4
            }]
        },
        options:{
            plugins:{legend:{display:false}},
            scales:{y:{beginAtZero:true,max:100}}
        }
    });
}


/* Reveal animation */

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



/* Sidebar toggle */

const toggle=document.getElementById("toggleSidebar");
const sidebar=document.getElementById("sidebar");

if(toggle){
    toggle.onclick=()=>{
        sidebar.classList.toggle("collapse");
    };
}

const username = localStorage.getItem("username");

if(username){
    document.getElementById("username").textContent = username;
}
function logout(){

    localStorage.clear();

    window.location.href="login.html";

}
