// main.js

const canvas = document.getElementById("gameCanvas");
const ctx = canvas.getContext("2d");

const keys = {
    
    w: false,
    a: false,
    s: false,
    d: false

};

window.addEventListener("keydown",function(event){

    if (event.key == "w") keys.w = true;
    if (event.key == "a") keys.a= true;
    if (event.key == "s") keys.s = true;
    if (event.key == "d") keys.d = true;

});

window.addEventListener("keyup", function(event) {
    
    if (event.key === "w") keys.w = false;
    if (event.key === "a") keys.a = false;
    if (event.key === "s") keys.s = false;
    if (event.key === "d") keys.d = false;

});

async function getFrameFromJava() {
    
    try {
    
        const url = `http://localhost:8080/frame?w=${keys.w}&a=${keys.a}&s=${keys.s}&d=${keys.d}`;
        const response = await fetch("http://localhost:8080/frame");
        const rayData = await response.json(); 

        draw3DWorld(rayData);

        requestAnimationFrame(getFrameFromJava);

    } catch (error) {
        
        console.error("Could not connect to the Java Server! Is it running?", error);

        setTimeout(getFrameFromJava, 2000);

    }
}

function draw3DWorld(rayData) {

    ctx.fillStyle = "#87ceeb";
    ctx.fillRect(0, 0, 640, 240); 

    ctx.fillStyle = "#555555";
    ctx.fillRect(0, 240, 640, 240); 

    for (let x = 0; x < rayData.length; x++) {

        let drawStart = rayData[x][0];
        let drawEnd = rayData[x][1];
        let side = rayData[x][2];

        let lineHeight = drawEnd - drawStart;

        if (side === 0) {
            ctx.fillStyle = "#cc0000"; 
        } else {
            ctx.fillStyle = "#770000"; 
        }

        ctx.fillRect(x, drawStart, 1, lineHeight); 
    }
}

getFrameFromJava();