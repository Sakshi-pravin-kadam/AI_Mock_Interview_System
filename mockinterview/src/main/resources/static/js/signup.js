function validateSignup(){

    let name = document.getElementById("name").value.trim();
    let email = document.getElementById("email").value.trim();
    let password = document.getElementById("password").value;
    let confirmPassword = document.getElementById("confirmPassword").value;

    // Name validation
    if(name === ""){
        alert("Full name is required");
        return false;
    }

    if(name.length < 3){
        alert("Name must be at least 3 characters");
        return false;
    }

    let namePattern = /^[A-Za-z\s]+$/;

    if(!namePattern.test(name)){
        alert("Name should contain only letters");
        return false;
    }

    // Email validation
    if(email === ""){
        alert("Email is required");
        return false;
    }

    let emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if(!emailPattern.test(email)){
        alert("Enter a valid email address");
        return false;
    }

    // Password validation
    if(password === ""){
        alert("Password is required");
        return false;
    }

    if(password.length < 8){
        alert("Password must be at least 8 characters");
        return false;
    }

    let passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])/;

    if(!passwordPattern.test(password)){
        alert("Password must contain uppercase, lowercase, number, and special character");
        return false;
    }

    if(password.includes(" ")){
        alert("Password cannot contain spaces");
        return false;
    }

    // Confirm password validation
    if(confirmPassword === ""){
        alert("Please confirm your password");
        return false;
    }

    if(password !== confirmPassword){
        alert("Passwords do not match");
        return false;
    }

    return true;
}
function registerUser(event){

    event.preventDefault();

    if(!validateSignup()){
        return false;
    }

    let name = document.getElementById("name").value.trim();
    let email = document.getElementById("email").value.trim();
    let password = document.getElementById("password").value;

    fetch("http://localhost:8080/api/auth/register",{
        method:"POST",
        headers:{
            "Content-Type":"application/json"
        },
        body: JSON.stringify({
            name:name,
            email:email,
            password:password
        })
    })
        .then(response => response.text())
        .then(data => {
            alert(data);

            if(data === "User registered successfully"){
                window.location.href = "/login.html";
            }
        })
        .catch(error=>{
            console.error("Error:",error);
        });

}