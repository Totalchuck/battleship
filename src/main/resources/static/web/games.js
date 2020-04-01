let ul = document.getElementById("output")
let li = document.createElement("li")
let myRealData =[];





$(function() {

    // display text in the output area
    function showOutput(text) {

        $("#output").text(text);

    }

    function playerEmail (x) {
        if (x.players.length != 0) {
            return x.players[0].email;
        }
    }

    function createNewLi(x) {
        let li = document.createElement("li")
        ul.appendChild(li);
        li.setAttribute("id", x.id)
        li.innerHTML = x.time+  " + " + playerEmail(x) ;


    }

    function loadGameData() {

        $.get("/api/games_view")
            .done(function (data) {
                let myData = JSON.stringify(data, null, 2);
                let myArray = JSON.parse(myData)
                console.log(myArray)

                myArray.forEach(game => createNewLi(game))
                myRealData = myArray;



            })

    }

    function loadPlayerScore() {

         $.get("http://localhost:8080/api/players_view")
             .done(function (data) {
                 let myData = JSON.stringify(data, null, 2);
                 let myArray = JSON.parse(myData)
                 console.log(myArray)

                 myArray.forEach(player => sumScores(player.score) + console.log(player.email))
             })
    }

    function sumScores (x) {
        let total = 0;
        for (let i =0 ; i<x.length ; i++) {
            total += x[i].score;

        }
        console.log(total)
    }

    loadGameData()

    loadPlayerScore()


})