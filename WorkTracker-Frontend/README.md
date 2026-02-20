# Work Tracker – Frontend

Vanilla HTML/CSS/JS frontend for the Work Tracker API.

## How to start the frontend

Run a **local static server** from this folder so the app is served over HTTP (required for calling the backend from another port). Use one of these:

**Option 1 – npx serve (no install)**  
```bash
cd WorkTracker-Frontend
npx serve .
```
Then open the URL it prints (e.g. http://localhost:3000).

**Option 2 – Python**  
```bash
cd WorkTracker-Frontend
python3 -m http.server 5500
```
Then open http://localhost:5500.

**Option 3 – VS Code Live Server**  
Right‑click `index.html` → “Open with Live Server” (if the extension is installed).

## Backend

Start the backend first so the API is available:

```bash
cd WorkTracker-Backend
./gradlew bootRun
```

The backend runs at http://localhost:8080. CORS is configured so the frontend (on a different port) can call the API.
