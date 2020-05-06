let ul = document.getElementById("output")
let li = document.createElement("li")
let myRealData =[];
let table = document.getElementById("table")
let username = 0;
let opponentInfo = [];
let opponentName = []


//Player Informations

   function getGamePlayerInformation() {
   getUserName()

    $.get("/api/games_view")
    .then (function(data) {
        let myData = JSON.stringify(data, null, 2);
        let myArray = JSON.parse(myData)
        console.log(myArray)




        myArray.forEach(game =>
            game.gamePlayers.forEach(gamePlayer =>
                { if (gamePlayer.playerMail == username && !game.gameOver) {
                        displayGameAndOpponentInformations(gamePlayer.Id, gamePlayer.id)

                        createNewLi(gamePlayer.gameId,gamePlayer.Id, gamePlayer.opponentName);
                        let GP = gamePlayer.id;

                        createRejoinGameBtn(GP)}





                    else if (gamePlayer.playerMail != username && game.gamePlayers.length == 1 ) {
                        createNewLi(gamePlayer.gameId,gamePlayer.Id, gamePlayer.playerMail) ;

                        let gameId = gamePlayer.gameId;
                        console.log(gamePlayer.gameId)
                        createJoinGameBtn(gamePlayer.gameId)}

                  }))
 })}



//Log in
document.getElementById("logIn").onclick = function() {logIn(document.getElementById("email").value, document.getElementById("password").value)};

        function logIn(email, password) {
    $.post("/api/login", { email: email, password: password })
    .done(function() { console.log("logged in"); window.location.reload(true) })
}

//Sign up
document.getElementById("signUp").onclick = function() {signUp(document.getElementById("email").value, document.getElementById("password").value)};

 function signUp(email, password) {
    $.post("/api/players", { email: email , password: password })
    .done(function() { console.log("Signed up") ; logIn(email, password);})


}



//log Out

document.getElementById("logOut").onclick = function() {logOut()};

       function logOut() {
 $.post("/api/logout").done(function() { console.log("logged out") ; window.location.reload(true) })  }

//Enter Game
document.getElementById("signUp").onclick = function() {signUp(document.getElementById("email").value, document.getElementById("password").value)};

 function signUp(email, password) {
    $.post("/api/players", { email: email , password: password })
    .done(function() { console.log("Signed up") ; logIn(email, password);})


}

//Create Game
 document.getElementById("createGame").onclick = function() {createGame(username)};

  function createGame(email) {
     $.post("/api/games", { email: email})
     .done(function() { console.log("Game created") ; window.location.reload(true) })


 }

 //Join Existing Game



function joinExistingGame(email, gameId) {
     $.post("/api/game/"+ gameId + "/players", { email: email, gameId : gameId})
     .done(function() { console.log("Game joined") ; window.location.reload(true) })

 }




    function playerEmail (x) {
        if (x.players.length != 0) {
            return x.players[0].email;
        }
    }

    function createNewLi(gameId, gamePlayerId, opponent) {
        let li = document.createElement("li")
        ul.appendChild(li);
        li.setAttribute("id", gameId)
        displayGameAndOpponentInformations(gameId, gamePlayerId)
        if (opponent == "No Opponent") {
            li.innerHTML = "game" + gameId + "waiting for an opponent"
        } else {
        li.innerHTML = "game" + gameId + " created at playing against "  + opponent;}

}

    function createJoinGameBtn (gameId) {
    let button = document.createElement("button")
    ul.appendChild(button);
    button.innerHTML = "join game with Id nr " + gameId + " and the player name "+ username;
    button.setAttribute("id", "joinExistingGame")
    button.setAttribute("gameId", gameId)
    document.getElementById("joinExistingGame").onclick = function() {joinExistingGame(username, gameId)};


    }

     function createRejoinGameBtn (gamePlayerId) {

    let a = document.createElement("a")
                             ul.appendChild(a);
                             a.innerHTML = "Rejoin game" + gamePlayerId
                             a.setAttribute("id", gamePlayerId)
                             a.setAttribute("href", '/web/game.html?gp=' + gamePlayerId)


          }

    function createTable(playerName, score) {
        let tr = document.createElement("tr")
        table.appendChild(tr);

        let td = document.createElement("td")
        tr.appendChild(td);
        td.innerHTML = playerName;

        let td2 = document.createElement("td")
        tr.appendChild(td2);
        td2.innerHTML = score;
    }


    function loadGameData() {
          $.get("/api/games_view").then(function (data) {
                let myData = JSON.stringify(data, null, 2);
                let myArray = JSON.parse(myData)
                console.log("test")
                myRealData = myArray;
            })
    }


//look for opponent informations

function displayGameAndOpponentInformations (gameId, playerId) {

    $.get("/api/games_view/" + gameId).done(function(data) {
        let myData = JSON.stringify(data);
        let myArray = JSON.parse(myData)
          myArray.gamePlayers.forEach(gamePlayer => {
            if (gamePlayer.id != playerId) {
                opponentInfo = gamePlayer;
                console.log(gamePlayer)
                console.log(gamePlayer.playerMail)
                opponentName = gamePlayer.playerMail
            }
        })
    })
}





    function getUserName() {

             $.get("/username")
                .then(function (data) {
                    let myData = JSON.stringify(data, null, 2);
                    let myArray = JSON.parse(myData)

                    username = myArray.email


                }).then(function() {

                }).then(function() {

              if (username != 0) {
                 document.getElementById("loginContainer").style.display = "none"
                 document.getElementById("logOut").style.display = "initial"}

})


        }

 function loadPlayerScore() {

         $.get("/api/players_view")
             .done(function (data) {
                 let myData = JSON.stringify(data, null, 2);
                 let myArray = JSON.parse(myData)
                console.log(myArray)
                myArray.forEach(player => console.log(player.score + player.email))
                 myArray.forEach(player => createTable(player.email, sumScores(player.score)))


             })
    }

    function sumScores (x) {
        let total = 0;
        for (let i =0 ; i<x.length ; i++) {
            total += x[i].score;


        }
        console.log(total)

        return total;
    }


      if (username == 0)  {
                 document.getElementById("logOut").style.display = "none"}


    getUserName()

    setTimeout(function(){ window.location.reload();; }, 100000);


    getGamePlayerInformation()
    loadPlayerScore()


