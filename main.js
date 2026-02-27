// main.js

const canvas = document.getElementById("gameCanvas");
const ctx = canvas.getContext("2d");

async function getFrameFromJava() {
    try {
        const response = await fetch("http://localhost:8080/frame");
        const rayData = await response.json(); 

        draw3DWorld(rayData);

    } catch (error) {
        console.error("Could not connect to the Java Server! Is it running?", error);
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