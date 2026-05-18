const api = "/api/payments";
const currency = new Intl.NumberFormat("en-LK", {
    style: "currency",
    currency: "LKR"
});

const paymentForm = document.querySelector("#paymentForm");
const paymentType = document.querySelector("#paymentType");
const cardFields = document.querySelector("#cardFields");
const cashFields = document.querySelector("#cashFields");
const formMessage = document.querySelector("#formMessage");
const paymentsTable = document.querySelector("#paymentsTable");
const emptyState = document.querySelector("#emptyState");
const refreshButton = document.querySelector("#refreshButton");

paymentType.addEventListener("change", togglePaymentFields);
paymentForm.addEventListener("submit", handlePaymentSubmit);
paymentForm.addEventListener("reset", () => {
    window.setTimeout(() => {
        togglePaymentFields();
        setMessage("");
    }, 0);
});
refreshButton.addEventListener("click", loadDashboard);

loadDashboard();
togglePaymentFields();

function togglePaymentFields() {
    const isCard = paymentType.value === "CARD";
    cardFields.classList.toggle("hidden", !isCard);
    cashFields.classList.toggle("hidden", isCard);

    cardFields.querySelectorAll("input, select").forEach((field) => {
        field.required = isCard && ["cardNumber", "cardHolderName", "expiryDate"].includes(field.name);
    });
    cashFields.querySelectorAll("input").forEach((field) => {
        field.required = !isCard;
    });
}

async function handlePaymentSubmit(event) {
    event.preventDefault();
    setMessage("Processing payment...");

    const formData = new FormData(paymentForm);
    const payload = Object.fromEntries(formData.entries());
    payload.amount = Number(payload.amount);
    payload.amountReceived = Number(payload.amountReceived || 0);

    if (payload.paymentType === "CARD") {
        delete payload.amountReceived;
    }

    try {
        const response = await fetch(api, {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(payload)
        });
        const result = await parseResponse(response);

        setMessage(result.processingMessage || "Payment saved successfully.");
        paymentForm.reset();
        togglePaymentFields();
        await loadDashboard();
    } catch (error) {
        setMessage(error.message, true);
    }
}

async function loadDashboard() {
    try {
        const [payments, report] = await Promise.all([
            fetchJson(api),
            fetchJson(`${api}/report`)
        ]);
        renderPayments(payments);
        renderReport(report);
    } catch (error) {
        setMessage(error.message, true);
    }
}

function renderPayments(payments) {
    paymentsTable.innerHTML = "";
    emptyState.style.display = payments.length ? "none" : "block";

    payments
        .slice()
        .reverse()
        .forEach((payment) => {
            const row = document.createElement("tr");
            const typeClass = payment.paymentType === "CARD" ? "card" : "cash";
            row.innerHTML = `
                <td><strong>${escapeHtml(payment.paymentId)}</strong></td>
                <td>${escapeHtml(payment.customerName)}<br><small>${escapeHtml(payment.customerId)}</small></td>
                <td>${escapeHtml(payment.appointmentId)}</td>
                <td><span class="pill ${typeClass}">${escapeHtml(payment.paymentType)}</span></td>
                <td>${currency.format(payment.amount)}</td>
                <td>
                    <select class="status-select" aria-label="Update payment status">
                        ${["PENDING", "COMPLETED", "FAILED", "REFUNDED"].map((status) => `
                            <option value="${status}" ${payment.status === status ? "selected" : ""}>${status}</option>
                        `).join("")}
                    </select>
                </td>
                <td>${escapeHtml(payment.paymentDate)}</td>
                <td><button class="danger-button" type="button">Delete</button></td>
            `;

            row.querySelector(".status-select").addEventListener("change", (event) => {
                updateStatus(payment.paymentId, event.target.value);
            });
            row.querySelector(".danger-button").addEventListener("click", () => {
                deletePayment(payment.paymentId);
            });
            paymentsTable.appendChild(row);
        });
}

function renderReport(report) {
    document.querySelector("#heroRevenue").textContent = currency.format(report.completedRevenue || 0);
    document.querySelector("#heroCount").textContent = `${report.totalPayments || 0} payment records`;
    document.querySelector("#completedRevenue").textContent = currency.format(report.completedRevenue || 0);
    document.querySelector("#totalPayments").textContent = report.totalPayments || 0;
    document.querySelector("#pendingPayments").textContent = report.pendingPayments || 0;
    document.querySelector("#cardPayments").textContent = report.cardPayments || 0;
    document.querySelector("#cashPayments").textContent = report.cashPayments || 0;
    document.querySelector("#completedPayments").textContent = report.completedPayments || 0;
    document.querySelector("#failedPayments").textContent = report.failedPayments || 0;
    document.querySelector("#refundedPayments").textContent = report.refundedPayments || 0;
}

async function updateStatus(paymentId, status) {
    try {
        await fetchJson(`${api}/${encodeURIComponent(paymentId)}/status`, {
            method: "PATCH",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({status})
        });
        setMessage(`Payment ${paymentId} updated to ${status}.`);
        await loadDashboard();
    } catch (error) {
        setMessage(error.message, true);
        await loadDashboard();
    }
}

async function deletePayment(paymentId) {
    const confirmed = window.confirm(`Delete payment ${paymentId}?`);
    if (!confirmed) {
        return;
    }

    try {
        const response = await fetch(`${api}/${encodeURIComponent(paymentId)}`, {
            method: "DELETE"
        });
        if (!response.ok) {
            await parseResponse(response);
        }
        setMessage(`Payment ${paymentId} deleted.`);
        await loadDashboard();
    } catch (error) {
        setMessage(error.message, true);
    }
}

async function fetchJson(url, options = {}) {
    const response = await fetch(url, options);
    return parseResponse(response);
}

async function parseResponse(response) {
    const text = await response.text();
    const data = text ? JSON.parse(text) : {};

    if (!response.ok) {
        throw new Error(data.message || "Request failed");
    }

    return data;
}

function setMessage(message, isError = false) {
    formMessage.textContent = message;
    formMessage.classList.toggle("error", isError);
}

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll("\"", "&quot;")
        .replaceAll("'", "&#039;");
}
