async function login() {
    const userEmail = document.getElementById("login").value;
    const userPassword = document.getElementById("password").value;

    fetch("/auth", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            login: userEmail,
            password: userPassword,
        }),
    })
     .then(function(response) {
          if(response.status!==200) {
              throw new Error(response.status)
          }
          location.reload();
     }).catch(function(error) {
          document.getElementById("error").innerHTML = error;
     });
}
