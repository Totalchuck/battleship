let square = document.getElementById("square");
let shipLocationGrid = document.getElementById("shipLocationGrid")
let salvoLocationGrid = document.getElementById("salvoLocationGrid")
let game = document.getElementById("game")
let player = document.getElementById("player")
let opponent = document.getElementById("opponent")
let turnPlayer = document.getElementById("turnPlayer")
let turnOpponent = document.getElementById("turnOpponent")
let alphaGrid = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];
let numGrip = [1,2,3,4,5,6,7,8,9,10]
let GamePlayerId =[];




function drawGrid(container, grid) {
    for (let i = 0; i < alphaGrid.length; i++) {
        let newSpan = document.createElement("span");
        container.appendChild(newSpan)
        for (let j = 0; j < numGrip.length; j++) {
            let newSquare = document.createElement("div")
            newSquare.setAttribute("id", grid + alphaGrid[i] + numGrip[j])
            newSquare.innerHTML = alphaGrid[i] + numGrip[j]
            newSpan.appendChild(newSquare)
        }
    }
}


function displayPlayerShipsAndSalvo(gameId) {
    $.get("http://localhost:8080/api/gamePlayer_view/" + gameId)
        .then(function (data) {
            let myData = JSON.stringify(data);
            let myArray = JSON.parse(myData);
            console.log(myArray);
            let shipsLocations = []
            let salvoLocations = []

            myArray.ships.forEach(ship => ship.location.forEach(shipLocation => shipsLocations.push(shipLocation)))
            myArray.salvos.forEach(salvo => salvo.salvoLocations.forEach(salvoLocation => salvoLocations.push(salvoLocation)))


            shipsLocations.forEach(place => document.getElementById("ship"+ place).setAttribute("class" , "green"))
            salvoLocations.forEach(place => document.getElementById("salvo"+ place).setAttribute("class" , "blue"))


            displayGameAndOpponentInformations(myArray.gameId, myArray.id)

            player.innerHTML ="The player "+ myArray.player.email + " playing the turn " + myArray.salvos[0].turnNumber


     })

}

//grab data from "game_view" and look for the opponent by elimintating the player from the loop
function displayGameAndOpponentInformations (gameId, playerId) {
    $.get("http://localhost:8080/api/games_view/" + gameId).then (function(data) {
        let myData = JSON.stringify(data);
        let myArray = JSON.parse(myData)

        //look for the opponent infos
        let opponentInfo = [];
        myArray.gamePlayers.forEach(gamePlayer => {
            if (gamePlayer.id != playerId) {
                opponentInfo = gamePlayer;
            }
        })

        //opponent salvo locations
        let opponentSalvosLocation = [];
        opponentInfo.salvos.forEach(salvo => opponentSalvosLocation.push(salvo.salvoLocations))
        console.log(opponentSalvosLocation[0])

        opponentSalvosLocation[0].forEach(location => document.getElementById("ship"+ location).setAttribute("class" , "orange"))

        //display opponent turn and name

        console.log(opponentInfo)
        opponent.innerHTML = "against the opponent " + opponentInfo.player.email + " playing his turn " + opponentInfo.salvos[0].turnNumber
    })

}


//Looks for "gp" in the URL
function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, '\\$&');
    var regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)'),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, ' '));
}


drawGrid(shipLocationGrid, "ship")
drawGrid(salvoLocationGrid, "salvo")
displayPlayerShipsAndSalvo(getParameterByName('gp'));
console.log(GamePlayerId)


