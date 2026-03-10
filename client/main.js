const canvas = document.getElementById("gameCanvas");
const ctx = canvas.getContext("2d");

const keys = {
    w: false,
    a: false,
    s: false,
    d: false
};

window.addEventListener("keydown", function(event){
    if (event.key == "w") keys.w = true;
    if (event.key == "a") keys.a = true;
    if (event.key == "s") keys.s = true;
    if (event.key == "d") keys.d = true;
});

window.addEventListener("keyup", function(event) {
    if (event.key === "w") keys.w = false;
    if (event.key === "a") keys.a = false;
    if (event.key === "s") keys.s = false;
    if (event.key === "d") keys.d = false;
});


let lastTime = performance.now();
let worldMap = [];

async function fetchMap() {
    try {
        const response = await fetch("http://localhost:8080/map");
        worldMap = await response.json();
    } catch (error) {
        console.error("Failed to load map data.", error);
    }
}

function drawRadar(playerX, playerY) {
    if (worldMap.length === 0) return;

    const radar = document.getElementById("radarCanvas");
    const rctx = radar.getContext("2d");

    // Clear background
    rctx.fillStyle = "#050505";
    rctx.fillRect(0, 0, 150, 150);

    const scale = 150 / worldMap.length;

    for (let x = 0; x < worldMap.length; x++) {
        for (let y = 0; y < worldMap[x].length; y++) {
            if (worldMap[x][y] > 0) {
                if (worldMap[x][y] === 2) rctx.fillStyle = "#00ff00"; // Entry
                else if (worldMap[x][y] === 3) rctx.fillStyle = "#0000ff"; // Exit
                else rctx.fillStyle = "#005544"; // Walls
                
                rctx.fillRect(x * scale, y * scale, scale, scale);
            }
        }
    }

    rctx.fillStyle = "#ffffff";
    rctx.beginPath();
    rctx.arc(playerX * scale, playerY * scale, 2, 0, Math.PI * 2);
    rctx.fill();
}

async function getFrameFromJava() {
    try {
        const url = `http://localhost:8080/frame?w=${keys.w}&a=${keys.a}&s=${keys.s}&d=${keys.d}`;
        const response = await fetch(url);
        const serverData = await response.json(); 

        document.getElementById("posX").innerText = serverData.x.toFixed(2);
        document.getElementById("posY").innerText = serverData.y.toFixed(2);

        const now = performance.now();
        const fps = Math.round(1000 / (now - lastTime)); 
        lastTime = now;
        document.getElementById("fpsCounter").innerText = fps;

        draw3DWorld(serverData.frame);
        drawRadar(serverData.x, serverData.y);

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
        let wallType = rayData[x][3];

        let lineHeight = drawEnd - drawStart;

        if (wallType === 2) {
            ctx.fillStyle = (side === 0) ? "#00ff00" : "#00aa00"; 
        } else if (wallType === 3) {
            ctx.fillStyle = (side === 0) ? "#0000ff" : "#0000aa"; 
        } else {
            ctx.fillStyle = (side === 0) ? "#cc0000" : "#770000"; 
        }

        ctx.fillRect(x, drawStart, 1, lineHeight); 
    }
}

fetchMap().then(() => {
    getFrameFromJava();
});