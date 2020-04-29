let square = document.getElementById("square")
let shipLocationGrid = document.getElementById("shipLocationGrid")
let salvoLocationGrid = document.getElementById("salvoLocationGrid")
let game = document.getElementById("game")
let player = document.getElementById("player")
let opponent = document.getElementById("opponent")
let turnPlayer = document.getElementById("turnPlayer")
let turnOpponent = document.getElementById("turnOpponent")
let alphaGrid = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];
let numGrip = [1,2,3,4,5,6,7,8,9,10]
let arrayGrid = []
let gamePlayerId = getParameterByName('gp');
let username = 0;
let horizontal = true;
let placingShipsEvent = false;
let placingShipPositionAllowed = false;
let shipsArray = [];
let shipSelection = null
let salvosArray = [];
let turnNumber = 0;
let gameId = 0;

let responseSalvos= null;


//turn Number
function fetchTurnNumber() {
     $.get("http://localhost:8080/api/gamePlayer_view/" + gamePlayerId)
            .then(function (data) {
                let myData = JSON.stringify(data);
                let myArray = JSON.parse(myData);
                console.log(myArray);
                turnNumber = myArray.turnGP;

}) }

//log Out

document.getElementById("logOut").onclick = function() {logOut()};

       function logOut() {
 $.post("/api/logout").done(function() { console.log("logged out") ; window.location = "/web/games.html" })  }


//Draw a grid
function drawGrid(container, grid) {
    for (let i = 0; i < alphaGrid.length; i++) {
        let newSpan = document.createElement("span");
        container.appendChild(newSpan)
        for (let j = 0; j < numGrip.length; j++) {
            let newSquare = document.createElement("div")
            newSquare.setAttribute("id", grid + alphaGrid[i] + numGrip[j])
            newSquare.innerHTML = alphaGrid[i] + numGrip[j]
            arrayGrid.push(alphaGrid[i] + numGrip[j])
            newSpan.appendChild(newSquare)
        }
    }
}

//change color of the field during the placement of the salvoes
function mouseOverPositionSalvo () {

     arrayGrid.forEach(location =>
           document.getElementById("salvo" + location).setAttribute("onmouseover", "changingColorBlue(this)"))

     arrayGrid.forEach(location =>
           document.getElementById("salvo" + location).setAttribute("onmouseout", "transparent(this)"))

     arrayGrid.forEach(location =>
           document.getElementById("salvo" + location).setAttribute("onClick", "placedSalvo(this)"))

}

//Send the fields that are chosen to an Array and stop the selection after 4
function placedSalvo(x)  {

    let yellowNum = document.querySelectorAll('[placed="yellow"]').length;

    if (yellowNum < 4) {
    x.setAttribute("placed", "yellow")
    console.log(x)
    salvosArray.push(x.id.slice(5,8))

    }
    else {
    console.log("too many salvoes")
    }

}

//change color of the field during the placement of the ships
function mouseOverPositionShip (horizontal) {

    if (horizontal) {

         arrayGrid.forEach(location =>
               document.getElementById("ship" + location).setAttribute("onmouseover", "changingColorBlue(this)"))

         arrayGrid.forEach(location =>
               document.getElementById("ship" + location).setAttribute("onmouseout", "transparent(this)"))

    } else if (!horizontal)  {

     arrayGrid.forEach(location =>
                    document.getElementById("ship" + location).setAttribute("onmouseover", "changingColorBlue(this)"))

         arrayGrid.forEach(location =>
           document.getElementById("ship" + location).setAttribute("onmouseout", "transparent(this)"))
   }

}

//Check if a ship was already placed and enable the placement of ships
function placingShip (shipType) {
        if (document.getElementById(shipType).className != "green") {

         mouseOverPositionShip (horizontal)
            shipSelection = shipType;


            placingShipsEvent = true;
        } else {
        console.log("ship already placed")
        }
 }

//Allow the replacement of ships
function replacingShip (shipType) {
       if (document.getElementById(shipType).className == "green") {

        document.getElementById(shipType).setAttribute("class", "transparent")

        shipsArray.forEach(ship => {if (ship[0] == shipType) {
                                          ship[1].forEach(location => document.getElementById("ship" + location).setAttribute("placed" , "transparent"))
                                               }})

        shipsArray.forEach(ship => {if (ship[0] == shipType) {
            shipsArray.splice( shipsArray.indexOf(ship), 1 );
            }})

        shipsArray.forEach(ship => ship[1].forEach(location => console.log(location)))

        mouseOverPositionShip (horizontal)
           shipSelection = shipType;


           placingShipsEvent = true;
       } else {
       console.log("ship already placed")
       }


}




//Switching between horizontal and vertical, while choosing the position of a ship
document.addEventListener('keydown', function (event) {
  if ( event.key === "ArrowLeft"|| event.key === "ArrowRight") {
    horizontal = !horizontal ;
    placingShipsEvent = true;
    mouseOverPositionShip (horizontal)
    let blue = document.querySelectorAll('[class="blue"]');
    blue.forEach(div => div.setAttribute ("class", "transparent"))
  }})

//Sending the position of a ship to an array of ships
document.addEventListener('keydown', function (event) {
  if ( event.key === "Enter" && placingShipsEvent && placingShipPositionAllowed) {
        let arrLocation = [];
        let blue = document.querySelectorAll('[class="blue"]');
        blue.forEach(div => arrLocation.push(div.id.slice(4,6)))
        blue.forEach(div => div.setAttribute("placed", "yellow"))
        blue.forEach(div => div.setAttribute("class", ""))
        let shipLocation = [];
        shipLocation.push(shipSelection);
        shipLocation.push(arrLocation);
        shipsArray.push(shipLocation);
        placingShipsEvent = false;
        document.getElementById(shipSelection).setAttribute("class", "green")
        shipSelection = null;
  }})

//change the colors of the field while placing the ships
function transparent(x) {
const index = arrayGrid.findIndex(position => position === x.id.slice(4,7));

if (horizontal && shipSelection == "carrier") {
          x.setAttribute("class", "transparent")
           document.getElementById("ship"+ arrayGrid[index-20]).setAttribute("class", "transparent")
           document.getElementById("ship"+ arrayGrid[index-10]).setAttribute("class", "transparent")
           document.getElementById("ship"+ arrayGrid[index+10]).setAttribute("class", "transparent")
           document.getElementById("ship"+ arrayGrid[index+20]).setAttribute("class", "transparent")
       } else if (!horizontal && shipSelection == "carrier") {
           x.setAttribute("class", "transparent")
           document.getElementById("ship"+ arrayGrid[index-2]).setAttribute("class", "transparent")
           document.getElementById("ship"+ arrayGrid[index-1]).setAttribute("class", "transparent")
           document.getElementById("ship"+ arrayGrid[index+1]).setAttribute("class", "transparent")
           document.getElementById("ship"+ arrayGrid[index+2]).setAttribute("class", "transparent")

       }

 if (horizontal && shipSelection == "battleship") {
     x.setAttribute("class", "transparent")
      document.getElementById("ship"+ arrayGrid[index-10]).setAttribute("class", "transparent")
      document.getElementById("ship"+ arrayGrid[index+10]).setAttribute("class", "transparent")
      document.getElementById("ship"+ arrayGrid[index+20]).setAttribute("class", "transparent")
  } else if (!horizontal && shipSelection == "battleship") {
      x.setAttribute("class", "transparent")
      document.getElementById("ship"+ arrayGrid[index-1]).setAttribute("class", "transparent")
      document.getElementById("ship"+ arrayGrid[index+1]).setAttribute("class", "transparent")
      document.getElementById("ship"+ arrayGrid[index+2]).setAttribute("class", "transparent")

  }

 if (horizontal && shipSelection == "cruiser") {
    x.setAttribute("class", "transparent")

     document.getElementById("ship"+ arrayGrid[index-10]).setAttribute("class", "transparent")
     document.getElementById("ship"+ arrayGrid[index+10]).setAttribute("class", "transparent")

 } else if (!horizontal && shipSelection == "cruiser") {
     x.setAttribute("class", "transparent")

     document.getElementById("ship"+ arrayGrid[index-1]).setAttribute("class", "transparent")
     document.getElementById("ship"+ arrayGrid[index+1]).setAttribute("class", "transparent")


 }

  if (horizontal && shipSelection == "submarine") {
     x.setAttribute("class", "transparent")

      document.getElementById("ship"+ arrayGrid[index-10]).setAttribute("class", "transparent")
      document.getElementById("ship"+ arrayGrid[index+10]).setAttribute("class", "transparent")

  } else if (!horizontal && shipSelection == "submarine") {
      x.setAttribute("class", "transparent")

      document.getElementById("ship"+ arrayGrid[index-1]).setAttribute("class", "transparent")
      document.getElementById("ship"+ arrayGrid[index+1]).setAttribute("class", "transparent")


  }

    if (horizontal && shipSelection == "destroyer") {
       x.setAttribute("class", "transparent")


        document.getElementById("ship"+ arrayGrid[index+10]).setAttribute("class", "transparent")

    } else if (!horizontal && shipSelection == "destroyer") {
        x.setAttribute("class", "transparent")


        document.getElementById("ship"+ arrayGrid[index+1]).setAttribute("class", "transparent")



    } else {
     x.setAttribute("class", "transparent")
    }

}

function changingColorBlue(x) {
    const index = arrayGrid.findIndex(position => position === x.id.slice(4,7));

 if (placingShipsEvent)  {
    if (horizontal && shipSelection == "carrier") {

         if (index ==2 ||index < 20 || index >= 80 ||  document.getElementById("ship"+ arrayGrid[index]).getAttribute("placed") == "yellow" || document.getElementById("ship"+ arrayGrid[index-20]).getAttribute("placed") == "yellow" || document.getElementById("ship"+ arrayGrid[index -10]).getAttribute("placed") == "yellow" || document.getElementById("ship"+ arrayGrid[index +10]).getAttribute("placed") == "yellow" || document.getElementById("ship"+ arrayGrid[index -20]).getAttribute("placed") == "yellow")  {
             x.setAttribute("class", "red")
             placingShipPositionAllowed = false;
         } else  {
             x.setAttribute("class", "blue")
             document.getElementById("ship"+ arrayGrid[index-20]).setAttribute("class", "blue")
             document.getElementById("ship"+ arrayGrid[index-10]).setAttribute("class", "blue")
             document.getElementById("ship"+ arrayGrid[index+10]).setAttribute("class", "blue")
             document.getElementById("ship"+ arrayGrid[index+20]).setAttribute("class", "blue")
             placingShipPositionAllowed = true;
         }
     } else if (!horizontal && shipSelection == "carrier"){
        if (index == 1 || index % 10 == 0 || (index+1) % 10 == 0 || index.toString().slice(1,2) == 1  || index.toString().slice(1,2) == 8 || index == 8 ||  document.getElementById("ship"+ arrayGrid[index]).getAttribute("placed") == "yellow" || document.getElementById("ship"+ arrayGrid[index-2]).getAttribute("placed") == "yellow" || document.getElementById("ship"+ arrayGrid[index -1]).getAttribute("placed") == "yellow" || document.getElementById("ship"+ arrayGrid[index +1]).getAttribute("placed") == "yellow" || document.getElementById("ship"+ arrayGrid[index -2]).getAttribute("placed") == "yellow") {
             x.setAttribute("class", "red")
             placingShipPositionAllowed = false;
         } else {
             x.setAttribute("class", "blue")
             document.getElementById("ship"+ arrayGrid[index-2]).setAttribute("class", "blue")
             document.getElementById("ship"+ arrayGrid[index-1]).setAttribute("class", "blue")
             document.getElementById("ship"+ arrayGrid[index+1]).setAttribute("class", "blue")
             document.getElementById("ship"+ arrayGrid[index+2]).setAttribute("class", "blue")
             placingShipPositionAllowed = true;
         }
 }

    if (horizontal && shipSelection == "battleship") {
         if (index < 10 || index >= 80  ||  document.getElementById("ship"+ arrayGrid[index]).getAttribute("placed") == "yellow" || document.getElementById("ship"+ arrayGrid[index -10]).getAttribute("placed") == "yellow" || document.getElementById("ship"+ arrayGrid[index +10]).getAttribute("placed") == "yellow" || document.getElementById("ship"+ arrayGrid[index  + 20]).getAttribute("placed") == "yellow") {
             x.setAttribute("class", "red")
             placingShipPositionAllowed = false;
         } else  {
             x.setAttribute("class", "blue")
             document.getElementById("ship"+ arrayGrid[index-10]).setAttribute("class", "blue")
             document.getElementById("ship"+ arrayGrid[index+10]).setAttribute("class", "blue")
             document.getElementById("ship"+ arrayGrid[index+20]).setAttribute("class", "blue")
             placingShipPositionAllowed = true;
         }
     } else if (!horizontal && shipSelection == "battleship"){
         if (index == 1 || index % 10 == 0 || (index+1) % 10 == 0   || index.toString().slice(1,2) == 8 || index == 8 || document.getElementById("ship"+ arrayGrid[index]).getAttribute("placed") == "yellow" || document.getElementById("ship"+ arrayGrid[index -1]).getAttribute("placed") == "yellow" || document.getElementById("ship"+ arrayGrid[index +1]).getAttribute("placed") == "yellow" || document.getElementById("ship"+ arrayGrid[index -2]).getAttribute("placed") == "yellow")   {
             x.setAttribute("class", "red")
             placingShipPositionAllowed = false;
         } else {
             x.setAttribute("class", "blue")
             document.getElementById("ship"+ arrayGrid[index-1]).setAttribute("class", "blue")
             document.getElementById("ship"+ arrayGrid[index+1]).setAttribute("class", "blue")
             document.getElementById("ship"+ arrayGrid[index+2]).setAttribute("class", "blue")
             placingShipPositionAllowed = true;
         }
 }

     if (horizontal && shipSelection == "cruiser" || horizontal && shipSelection == "submarine") {
           if (index < 10 || index >= 90 ||  document.getElementById("ship"+ arrayGrid[index]).getAttribute("placed") == "yellow" ||  document.getElementById("ship"+ arrayGrid[index -10]).getAttribute("placed") == "yellow" || document.getElementById("ship"+ arrayGrid[index +10]).getAttribute("placed") == "yellow" ) {
               x.setAttribute("class", "red")
               placingShipPositionAllowed = false;
           } else  {
               x.setAttribute("class", "blue")
               document.getElementById("ship"+ arrayGrid[index-10]).setAttribute("class", "blue")
               document.getElementById("ship"+ arrayGrid[index+10]).setAttribute("class", "blue")
               placingShipPositionAllowed = true;
           }
       } else if (!horizontal && shipSelection == "cruiser" || !horizontal && shipSelection == "submarine"){
           if (index%10 ==0 || (index+1) % 10 == 0 || document.getElementById("ship"+ arrayGrid[index]).getAttribute("placed") == "yellow" || document.getElementById("ship"+ arrayGrid[index -1]).getAttribute("placed") == "yellow" || document.getElementById("ship"+ arrayGrid[index +1]).getAttribute("placed") == "yellow" ) {
               x.setAttribute("class", "red")
               placingShipPositionAllowed = false;
           } else {
               x.setAttribute("class", "blue")
               document.getElementById("ship"+ arrayGrid[index-1]).setAttribute("class", "blue")
               document.getElementById("ship"+ arrayGrid[index+1]).setAttribute("class", "blue")
               placingShipPositionAllowed = true;
           }
   }

   if (horizontal && shipSelection == "destroyer") {
           if ( index >= 90 || document.getElementById("ship"+ arrayGrid[index]).getAttribute("placed") == "yellow" || document.getElementById("ship"+ arrayGrid[index +10]).getAttribute("placed") == "yellow" ) {
               x.setAttribute("class", "red")
               placingShipPositionAllowed = false;
           } else  {
               x.setAttribute("class", "blue")
               document.getElementById("ship"+ arrayGrid[index+10]).setAttribute("class", "blue")
               placingShipPositionAllowed = true;
           }
       } else if (!horizontal && shipSelection == "destroyer"){
           if ( /*index%10 ==0 ||*/ (index+1) % 10 == 0  || document.getElementById("ship"+ arrayGrid[index]).getAttribute("placed") == "yellow" || document.getElementById("ship"+ arrayGrid[index +1]).getAttribute("placed") == "yellow" ) {
               x.setAttribute("class", "red")
               placingShipPositionAllowed = false;
           } else {
               x.setAttribute("class", "blue")
              document.getElementById("ship"+ arrayGrid[index+1]).setAttribute("class", "blue")
              placingShipPositionAllowed = true;
           }
   }} else {
   x.setAttribute("class", "blue")
   placingShipPositionAllowed = true;

   }


}

//send one Ship Array to Backend
function placeShip(shipType, location, horizontal ) {
    $.post("http://localhost:8080/api/games/players/" + (gamePlayerId - 1) + "/ships",
        { email: username, gamePlayerId : gamePlayerId, shipType : shipType, location : location, horizontal: horizontal})
        .done(function() { console.log("Ship placed") })

}

//check if the Array of Ships contains all the ship and send all of them to the backend(with placeShip())
function sendShip(shipsArray) {
    if (shipsArray.length == 5) {
        window.location.reload();
        shipsArray.forEach(ship => {
            placeShip(ship[0], ship[1].toString(), true) })
    }  else {
        console.log("please place all the ships")
    }
}

//Enable the placement of Salvos
function placingSalvos (location) {
    mouseOverPositionSalvo()
}

//send one salvo Array to the backend
function placeSalvos(locations, turnNumber) {
    $.post("http://localhost:8080/api/games/players/" + (gameId) + "/" + (gamePlayerId) +  "/salvos",
        { email: username, gamePlayerId : gamePlayerId, locations : locations , turn : turnNumber}).then(response =>
               response.substring(2, response.length-1).replace(/ /g, "").split(",").forEach(location =>
                    document.getElementById("salvo" + location).setAttribute("placed", "orange"))  ) }

//check if the salvo actually hit a ship
function salvosHit (locations) {
    $.post("http://localhost:8080/api/games/players/" + (gameId) +"/"+ gamePlayerId + "/salvos",
        { locations : locations }).then(response =>
            response.substring(2,response.length-1).replace(/ /g, "").split(",").forEach(location =>
                document.getElementById("salvo" + location).setAttribute("hit", "red"))
       )}


//check if the array of salvos contains exactly 4 salvos , send to the backend, and check if a salvo hit something
function sendSalvos(salvosArray) {
    if (salvosArray.length == 4)  {
        placeSalvos(salvosArray.toString(), turnNumber)
        salvosHit(salvosArray.toString())
        salvosArray.length = 0;
        displayPlayerShipsAndSalvo(gameId)
        window.location.reload();
    } else {
        console.log("please place all the salvos")
    }
}

//Fetch position of the player ship and Salvo from the backend to display them
function displayPlayerShipsAndSalvo(gameId) {
    $.get("http://localhost:8080/api/gamePlayer_view/" + gameId)
        .then(function (data) {
            let myData = JSON.stringify(data);
            let myArray = JSON.parse(myData);

            let shipsLocations = []
            let salvoLocations = []
            let salvoHitLocation = []
            let sunkShips = []

            myArray.ships.forEach(ship => ship.location.forEach(shipLocation => shipsLocations.push(shipLocation)))
            myArray.salvos.forEach(salvo => salvo.salvoLocations.forEach(salvoLocation => salvoLocations.push(salvoLocation)))

            myArray.salvoHitLocations.forEach(place => document.getElementById("salvo"+ place).setAttribute("hit" , "red"))

            myArray.opponentShipsSunk.forEach(opponentShipSunkLocation => sunkShips.push(opponentShipSunkLocation))

            shipsLocations.forEach(place => document.getElementById("ship"+ place).setAttribute("class" , "green"))
            salvoLocations.forEach(place => document.getElementById("salvo"+ place).setAttribute("salvo" , "orange"))


            if (sunkShips.length>0) {
            sunkShips.forEach(place => document.getElementById("salvo"+ place).setAttribute("sunk" , "black"))
            }

            console.log(myArray.gameEnd)

            console.log(myArray.gameWon)


            displayGameAndOpponentInformations(myArray.gameId, myArray.id)



     })

}

//grab data from "game_view" and look for the opponent by elimiatating the player from the loop
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

function getGameId() {
         $.get("http://localhost:8080/api/gamePlayer_view/" + gamePlayerId )
                .then(function (data) {
                    let myData = JSON.stringify(data, null, 2);
                    let myArray = JSON.parse(myData)

                    gameId = myArray.gameId;
})}

function getUserName() {
    $.get("/username")
         .then(function (data) {
               let myData = JSON.stringify(data, null, 2);
                    let myArray = JSON.parse(myData)
                    username = myArray.email
         })}



drawGrid(shipLocationGrid, "ship")
drawGrid(salvoLocationGrid, "salvo")
displayPlayerShipsAndSalvo(getParameterByName('gp'));
getUserName()
getGameId()




