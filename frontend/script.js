// ===========================
// LOAD ENTRIES FROM DB
// ===========================
async function loadEntries() {
    const email = localStorage.getItem("email");
    if (!email) return;

    const res = await fetch(`http://localhost:5000/list?email=${email}`);
    const data = await res.json();

    console.log("Loaded:", data);

    const tbody = document.getElementById("tableBody");
    tbody.innerHTML = ""; 

    data.forEach(app => {
        const row = document.createElement("tr");
        row.setAttribute("data-id", app.id);

        row.innerHTML = `
            <td>${app.company}</td>
            <td>${app.role}</td>
            <td>${app.type}</td>

            <td>
                <select onchange="updateStatus(${app.id}, this.value)">
                    <option ${app.status=='Applied'?'selected':''}>Applied</option>
                    <option ${app.status=='Accepted'?'selected':''}>Accepted</option>
                    <option ${app.status=='Rejected'?'selected':''}>Rejected</option>
                    <option ${app.status=='1st Round'?'selected':''}>1st Round</option>
                    <option ${app.status=='2nd Round'?'selected':''}>2nd Round</option>
                </select>
            </td>

            <td>${app.applied_date}</td>
            <td>${app.interview_date}</td>
        `;

        tbody.appendChild(row);
    });
}


// ===========================
// SAVE NEW ENTRY
// ===========================
async function saveEntry() {

    const email = localStorage.getItem("email");
    if (!email) {
        alert("Login first.");
        return;
    }

    const r = document.getElementById("newRow");

    const data = {
        email: email,
        company: r.querySelector(".company").value,
        role: r.querySelector(".role").value,
        type: r.querySelector(".type").value,
        status: r.querySelector(".status").value,
        applied_date: r.querySelector(".applied").value,
        interview_date: r.querySelector(".interview").value
    };

    console.log("Saving:", data);

    // Save to backend
    await fetch("http://localhost:5000/add", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(data)
    });

    // Add row visually ABOVE
    const tbody = document.getElementById("tableBody");
    const savedRow = document.createElement("tr");

    savedRow.innerHTML = `
        <td>${data.company}</td>
        <td>${data.role}</td>
        <td>${data.type}</td>
        <td>
            <select onchange="updateStatusTemp(this)">
                <option>Applied</option>
                <option>Accepted</option>
                <option>Rejected</option>
                <option>1st Round</option>
                <option>2nd Round</option>
            </select>
        </td>
        <td>${data.applied_date}</td>
        <td>${data.interview_date}</td>
    `;

    tbody.appendChild(savedRow);

    // Reset blank input row
    r.querySelector(".company").value = "";
    r.querySelector(".role").value = "";
    r.querySelector(".type").value = "Internship";
    r.querySelector(".status").value = "Applied";
    r.querySelector(".applied").value = "";
    r.querySelector(".interview").value = "";
}


// ===========================
// UPDATE STATUS (Existing rows)
// ===========================
async function updateStatus(id, newStatus) {

    await fetch("http://localhost:5000/update", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({id: id, status: newStatus})
    });

    if (newStatus === "Accepted") confetti();
    if (newStatus === "Rejected") showPopup("Don't worry. Every failure pushes you closer.");
}


// TEMP local confetti for freshly added rows
function updateStatusTemp(sel){
    if(sel.value === "Accepted") confetti();
    if(sel.value === "Rejected") showPopup("Keep going. Your effort matters!");
}


// ===========================
// POPUP
// ===========================
function showPopup(msg) {
    document.getElementById("popupText").innerText = msg;
    document.getElementById("popup").classList.remove("hidden");
}

function closePopup() {
    document.getElementById("popup").classList.add("hidden");
}

function confetti() {
    alert("🎉 Congrats!");
}


// Load existing entries on page open
window.onload = loadEntries;
