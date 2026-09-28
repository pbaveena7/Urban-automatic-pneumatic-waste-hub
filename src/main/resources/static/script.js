// Global API Base URL (Relative Path per requirements)
const API_BASE = "/api/pneumatic-waste";

// On page initialization
document.addEventListener("DOMContentLoaded", () => {
    loadDashboard();
});

// Helper for HTTP requests with strict error handling & logging
async function fetchAPI(url, options = {}) {
    try {
        const response = await fetch(url, options);
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || `HTTP Error ${response.status}`);
        }
        // Handle empty response (e.g., DELETE 200 OK)
        const contentType = response.headers.get("content-type");
        if (contentType && contentType.includes("application/json")) {
            return await response.json();
        }
        return null;
    } catch (error) {
        console.error("API Request Failed:", error);
        alert("Error: " + error.message);
        throw error;
    }
}

// --------------------------------------------------
// TAB SWITCHING LOGIC
// --------------------------------------------------
function switchTab(tabName) {
    // Hide all contents
    document.querySelectorAll(".tab-content").forEach(el => el.classList.remove("active"));
    // Deactivate all buttons
    document.querySelectorAll(".nav-tab").forEach(el => el.classList.remove("active"));

    // Activate selected content and button
    const targetContent = document.getElementById(`tab-${tabName}`);
    if (targetContent) {
        targetContent.classList.add("active");
    }
    
    const targetButton = Array.from(document.querySelectorAll(".nav-tab"))
        .find(btn => btn.getAttribute("onclick") && btn.getAttribute("onclick").includes(`'${tabName}'`));
    if (targetButton) {
        targetButton.classList.add("active");
    }

    // Load data for the selected tab
    switch (tabName) {
        case "dashboard":
            loadDashboard();
            break;
        case "contacts":
            loadContacts();
            break;
        case "buildings":
            loadBuildings();
            break;
        case "zones":
            loadZones();
            break;
        case "services":
            loadServices();
            break;
        case "waste-collections":
            loadWasteCollections();
            break;
        case "suction-cycles":
            loadSuctionCycles();
            break;
        case "purchase-orders":
            loadPurchaseOrders();
            break;
        case "vendor-bills":
            loadVendorBills();
            break;
        case "sales-orders":
            loadSalesOrders();
            break;
        case "invoices":
            loadInvoices();
            break;
        case "payments":
            loadPayments();
            break;
        case "accounts":
            loadAccounts();
            break;
        case "journal-entries":
            loadJournalEntries();
            break;
        case "budgets":
            loadBudgets();
            break;
        case "reports":
            // Reports loaded via dedicated buttons
            break;
    }
}

// --------------------------------------------------
// 1. DASHBOARD
// --------------------------------------------------
async function loadDashboard() {
    try {
        const stats = await fetchAPI(`${API_BASE}/reports/dashboard`);
        if (stats) {
            document.getElementById("dash-total-waste").innerText = `${stats.totalWasteKg || 0} KG`;
            document.getElementById("dash-active-zones").innerText = stats.activeZones || 0;
            document.getElementById("dash-suction-cycles").innerText = stats.completedSuctionCycles || 0;
            document.getElementById("dash-revenue").innerText = `₹${(stats.totalRevenue || 0).toLocaleString('en-IN', {minimumFractionDigits: 2})}`;
            document.getElementById("dash-expenses").innerText = `₹${(stats.totalExpenses || 0).toLocaleString('en-IN', {minimumFractionDigits: 2})}`;
            document.getElementById("dash-net-profit").innerText = `₹${(stats.netProfit || 0).toLocaleString('en-IN', {minimumFractionDigits: 2})}`;
            document.getElementById("dash-pending-invoices").innerText = stats.unpaidInvoicesCount || 0;
            document.getElementById("dash-pending-bills").innerText = stats.unpaidVendorBillsCount || 0;
        }
    } catch (e) {
        // Handled in fetchAPI
    }
}

// --------------------------------------------------
// 2. CONTACTS
// --------------------------------------------------
async function loadContacts() {
    const contacts = await fetchAPI(`${API_BASE}/contacts`);
    const tbody = document.getElementById("contacts-tbody");
    tbody.innerHTML = "";
    if (contacts) {
        contacts.forEach(c => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${c.id}</td>
                <td><strong>${escapeHtml(c.name)}</strong></td>
                <td><span class="badge ${c.contactType === 'CUSTOMER' ? 'badge-info' : 'badge-success'}">${c.contactType}</span></td>
                <td>${escapeHtml(c.phone || '')}</td>
                <td>${escapeHtml(c.email || '')}</td>
                <td>${escapeHtml(c.address || '')}</td>
                <td>
                    <button class="btn btn-secondary btn-sm" onclick="editContact(${c.id}, '${escapeHtml(c.name)}', '${c.contactType}', '${escapeHtml(c.phone || '')}', '${escapeHtml(c.email || '')}', '${escapeHtml(c.address || '')}')">Edit</button>
                    <button class="btn btn-danger btn-sm" onclick="deleteContact(${c.id})">Delete</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    }
}

async function handleContactSubmit(event) {
    event.preventDefault();
    const id = document.getElementById("contact-id").value;
    const contactData = {
        name: document.getElementById("contact-name").value,
        contactType: document.getElementById("contact-type").value,
        phone: document.getElementById("contact-phone").value,
        email: document.getElementById("contact-email").value,
        address: document.getElementById("contact-address").value
    };

    console.log("Sending Contact:", contactData);

    const method = id ? "PUT" : "POST";
    const url = id ? `${API_BASE}/contacts/${id}` : `${API_BASE}/contacts`;

    await fetchAPI(url, {
        method: method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(contactData)
    });

    alert(id ? "Contact updated successfully!" : "Contact created successfully!");
    resetContactForm();
    loadContacts();
}

function editContact(id, name, type, phone, email, address) {
    document.getElementById("contact-id").value = id;
    document.getElementById("contact-name").value = name;
    document.getElementById("contact-type").value = type;
    document.getElementById("contact-phone").value = phone;
    document.getElementById("contact-email").value = email;
    document.getElementById("contact-address").value = address;
    document.getElementById("contact-form-title").innerText = `Edit Contact #${id}`;
}

function resetContactForm() {
    document.getElementById("contact-id").value = "";
    document.getElementById("contact-form").reset();
    document.getElementById("contact-form-title").innerText = "Add New Contact";
}

async function deleteContact(id) {
    if (confirm(`Are you sure you want to delete Contact #${id}?`)) {
        await fetchAPI(`${API_BASE}/contacts/${id}`, { method: "DELETE" });
        alert("Contact deleted.");
        loadContacts();
    }
}

// --------------------------------------------------
// 3. BUILDINGS
// --------------------------------------------------
async function loadBuildings() {
    const buildings = await fetchAPI(`${API_BASE}/buildings`);
    const tbody = document.getElementById("buildings-tbody");
    tbody.innerHTML = "";
    if (buildings) {
        buildings.forEach(b => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${b.id}</td>
                <td><strong>${escapeHtml(b.buildingName)}</strong></td>
                <td>${escapeHtml(b.address || '')}</td>
                <td>${b.contactId || ''}</td>
                <td>${b.zoneId || ''}</td>
                <td>
                    <button class="btn btn-secondary btn-sm" onclick="editBuilding(${b.id}, '${escapeHtml(b.buildingName)}', '${escapeHtml(b.address || '')}', ${b.contactId}, ${b.zoneId})">Edit</button>
                    <button class="btn btn-danger btn-sm" onclick="deleteBuilding(${b.id})">Delete</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    }
}

async function handleBuildingSubmit(event) {
    event.preventDefault();
    const id = document.getElementById("building-id").value;
    const buildingData = {
        buildingName: document.getElementById("building-name").value,
        address: document.getElementById("building-address").value,
        contactId: parseInt(document.getElementById("building-contact-id").value),
        zoneId: parseInt(document.getElementById("building-zone-id").value)
    };

    console.log("Sending Building:", buildingData);

    const method = id ? "PUT" : "POST";
    const url = id ? `${API_BASE}/buildings/${id}` : `${API_BASE}/buildings`;

    await fetchAPI(url, {
        method: method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(buildingData)
    });

    alert(id ? "Building updated successfully!" : "Building added successfully!");
    resetBuildingForm();
    loadBuildings();
}

function editBuilding(id, name, address, contactId, zoneId) {
    document.getElementById("building-id").value = id;
    document.getElementById("building-name").value = name;
    document.getElementById("building-address").value = address;
    document.getElementById("building-contact-id").value = contactId;
    document.getElementById("building-zone-id").value = zoneId;
    document.getElementById("building-form-title").innerText = `Edit Building #${id}`;
}

function resetBuildingForm() {
    document.getElementById("building-id").value = "";
    document.getElementById("building-form").reset();
    document.getElementById("building-form-title").innerText = "Add New Building";
}

async function deleteBuilding(id) {
    if (confirm(`Are you sure you want to delete Building #${id}?`)) {
        await fetchAPI(`${API_BASE}/buildings/${id}`, { method: "DELETE" });
        alert("Building deleted.");
        loadBuildings();
    }
}

// --------------------------------------------------
// 4. ZONES
// --------------------------------------------------
async function loadZones() {
    const zones = await fetchAPI(`${API_BASE}/zones`);
    const tbody = document.getElementById("zones-tbody");
    tbody.innerHTML = "";
    if (zones) {
        zones.forEach(z => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${z.id}</td>
                <td><strong>${escapeHtml(z.zoneName)}</strong></td>
                <td>${escapeHtml(z.description || '')}</td>
                <td><span class="badge ${z.status === 'ACTIVE' ? 'badge-success' : 'badge-danger'}">${z.status}</span></td>
                <td>
                    <button class="btn btn-secondary btn-sm" onclick="editZone(${z.id}, '${escapeHtml(z.zoneName)}', '${escapeHtml(z.description || '')}', '${z.status}')">Edit</button>
                    <button class="btn btn-danger btn-sm" onclick="deleteZone(${z.id})">Delete</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    }
}

async function handleZoneSubmit(event) {
    event.preventDefault();
    const id = document.getElementById("zone-id").value;
    const zoneData = {
        zoneName: document.getElementById("zone-name").value,
        description: document.getElementById("zone-description").value,
        status: document.getElementById("zone-status").value
    };

    console.log("Sending Zone:", zoneData);

    const method = id ? "PUT" : "POST";
    const url = id ? `${API_BASE}/zones/${id}` : `${API_BASE}/zones`;

    await fetchAPI(url, {
        method: method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(zoneData)
    });

    alert(id ? "Zone updated successfully!" : "Zone created successfully!");
    resetZoneForm();
    loadZones();
}

function editZone(id, name, desc, status) {
    document.getElementById("zone-id").value = id;
    document.getElementById("zone-name").value = name;
    document.getElementById("zone-description").value = desc;
    document.getElementById("zone-status").value = status;
    document.getElementById("zone-form-title").innerText = `Edit Zone #${id}`;
}

function resetZoneForm() {
    document.getElementById("zone-id").value = "";
    document.getElementById("zone-form").reset();
    document.getElementById("zone-form-title").innerText = "Add New Zone";
}

async function deleteZone(id) {
    if (confirm(`Are you sure you want to delete Zone #${id}?`)) {
        await fetchAPI(`${API_BASE}/zones/${id}`, { method: "DELETE" });
        alert("Zone deleted.");
        loadZones();
    }
}

// --------------------------------------------------
// 5. SERVICES
// --------------------------------------------------
async function loadServices() {
    const services = await fetchAPI(`${API_BASE}/services`);
    const tbody = document.getElementById("services-tbody");
    tbody.innerHTML = "";
    if (services) {
        services.forEach(s => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${s.id}</td>
                <td><strong>${escapeHtml(s.serviceName)}</strong></td>
                <td><span class="badge badge-info">${s.serviceType}</span></td>
                <td>₹${(s.price || 0).toFixed(2)}</td>
                <td>${escapeHtml(s.description || '')}</td>
                <td>
                    <button class="btn btn-secondary btn-sm" onclick="editService(${s.id}, '${escapeHtml(s.serviceName)}', '${s.serviceType}', ${s.price}, '${escapeHtml(s.description || '')}')">Edit</button>
                    <button class="btn btn-danger btn-sm" onclick="deleteService(${s.id})">Delete</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    }
}

async function handleServiceSubmit(event) {
    event.preventDefault();
    const id = document.getElementById("service-id").value;
    const serviceData = {
        serviceName: document.getElementById("service-name").value,
        serviceType: document.getElementById("service-type").value,
        price: parseFloat(document.getElementById("service-price").value),
        description: document.getElementById("service-description").value
    };

    console.log("Sending Service:", serviceData);

    const method = id ? "PUT" : "POST";
    const url = id ? `${API_BASE}/services/${id}` : `${API_BASE}/services`;

    await fetchAPI(url, {
        method: method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(serviceData)
    });

    alert(id ? "Service updated!" : "Service added!");
    resetServiceForm();
    loadServices();
}

function editService(id, name, type, price, desc) {
    document.getElementById("service-id").value = id;
    document.getElementById("service-name").value = name;
    document.getElementById("service-type").value = type;
    document.getElementById("service-price").value = price;
    document.getElementById("service-description").value = desc;
    document.getElementById("service-form-title").innerText = `Edit Service #${id}`;
}

function resetServiceForm() {
    document.getElementById("service-id").value = "";
    document.getElementById("service-form").reset();
    document.getElementById("service-form-title").innerText = "Add New Service";
}

async function deleteService(id) {
    if (confirm(`Delete Service #${id}?`)) {
        await fetchAPI(`${API_BASE}/services/${id}`, { method: "DELETE" });
        alert("Service deleted.");
        loadServices();
    }
}

// --------------------------------------------------
// 6. WASTE COLLECTION
// --------------------------------------------------
async function loadWasteCollections() {
    const list = await fetchAPI(`${API_BASE}/waste-collections`);
    const tbody = document.getElementById("waste-tbody");
    tbody.innerHTML = "";
    if (list) {
        list.forEach(w => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${w.id}</td>
                <td>${w.buildingId}</td>
                <td>${w.zoneId}</td>
                <td><strong>${w.weightKg} KG</strong></td>
                <td><span class="badge badge-info">${w.wasteType}</span></td>
                <td>${w.collectionDate || ''}</td>
                <td><span class="badge badge-success">${w.collectionStatus}</span></td>
            `;
            tbody.appendChild(tr);
        });
    }
}

async function handleWasteSubmit(event) {
    event.preventDefault();
    const wasteData = {
        buildingId: parseInt(document.getElementById("waste-building-id").value),
        zoneId: parseInt(document.getElementById("waste-zone-id").value),
        weightKg: parseFloat(document.getElementById("waste-weight").value),
        wasteType: document.getElementById("waste-type").value,
        collectionDate: document.getElementById("waste-date").value || null,
        collectionStatus: document.getElementById("waste-status").value || "COLLECTED"
    };

    console.log("Sending Waste Collection:", wasteData);

    await fetchAPI(`${API_BASE}/waste-collections`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(wasteData)
    });

    alert("Waste collection recorded successfully!");
    document.getElementById("waste-form").reset();
    loadWasteCollections();
}

// --------------------------------------------------
// 7. SUCTION CYCLES
// --------------------------------------------------
async function loadSuctionCycles() {
    const cycles = await fetchAPI(`${API_BASE}/suction-cycles`);
    const tbody = document.getElementById("suction-tbody");
    tbody.innerHTML = "";
    if (cycles) {
        cycles.forEach(c => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${c.id}</td>
                <td>${c.zoneId}</td>
                <td>${formatDateTime(c.startTime)}</td>
                <td>${formatDateTime(c.endTime)}</td>
                <td><strong>${c.totalWasteKg} KG</strong></td>
                <td><span class="badge ${c.status === 'COMPLETED' ? 'badge-success' : 'badge-info'}">${c.status}</span></td>
            `;
            tbody.appendChild(tr);
        });
    }
}

async function handleSuctionSubmit(event) {
    event.preventDefault();
    const suctionData = {
        zoneId: parseInt(document.getElementById("suction-zone-id").value),
        totalWasteKg: parseFloat(document.getElementById("suction-weight").value),
        startTime: document.getElementById("suction-start").value || null,
        endTime: document.getElementById("suction-end").value || null,
        status: document.getElementById("suction-status").value
    };

    console.log("Sending Suction Cycle:", suctionData);

    await fetchAPI(`${API_BASE}/suction-cycles`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(suctionData)
    });

    alert("Suction cycle saved successfully!");
    document.getElementById("suction-form").reset();
    loadSuctionCycles();
}

// --------------------------------------------------
// 8. PURCHASE ORDERS
// --------------------------------------------------
async function loadPurchaseOrders() {
    const orders = await fetchAPI(`${API_BASE}/purchase-orders`);
    const tbody = document.getElementById("po-tbody");
    tbody.innerHTML = "";
    if (orders) {
        orders.forEach(po => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${po.id}</td>
                <td>${po.vendorId}</td>
                <td>${po.serviceId}</td>
                <td>${escapeHtml(po.description || '')}</td>
                <td>${po.quantity || 1}</td>
                <td>₹${(po.amount || 0).toFixed(2)}</td>
                <td>${po.orderDate || ''}</td>
                <td><span class="badge badge-info">${po.status}</span></td>
                <td>
                    <button class="btn btn-secondary btn-sm" onclick="editPO(${po.id}, ${po.vendorId}, ${po.serviceId}, '${escapeHtml(po.description || '')}', ${po.quantity}, ${po.amount}, '${po.orderDate || ''}', '${po.status}')">Edit</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    }
}

async function handlePOSubmit(event) {
    event.preventDefault();
    const id = document.getElementById("po-id").value;
    const poData = {
        vendorId: parseInt(document.getElementById("po-vendor-id").value),
        serviceId: parseInt(document.getElementById("po-service-id").value),
        description: document.getElementById("po-description").value,
        quantity: parseInt(document.getElementById("po-quantity").value),
        amount: parseFloat(document.getElementById("po-amount").value),
        orderDate: document.getElementById("po-date").value || null,
        status: document.getElementById("po-status").value
    };

    console.log("Sending Purchase Order:", poData);

    const method = id ? "PUT" : "POST";
    const url = id ? `${API_BASE}/purchase-orders/${id}` : `${API_BASE}/purchase-orders`;

    await fetchAPI(url, {
        method: method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(poData)
    });

    alert(id ? "Purchase Order updated!" : "Purchase Order created!");
    resetPOForm();
    loadPurchaseOrders();
}

function editPO(id, vendorId, serviceId, desc, qty, amount, date, status) {
    document.getElementById("po-id").value = id;
    document.getElementById("po-vendor-id").value = vendorId;
    document.getElementById("po-service-id").value = serviceId;
    document.getElementById("po-description").value = desc;
    document.getElementById("po-quantity").value = qty;
    document.getElementById("po-amount").value = amount;
    document.getElementById("po-date").value = date;
    document.getElementById("po-status").value = status;
    document.getElementById("po-form-title").innerText = `Edit Purchase Order #${id}`;
}

function resetPOForm() {
    document.getElementById("po-id").value = "";
    document.getElementById("po-form").reset();
    document.getElementById("po-form-title").innerText = "Create Purchase Order";
}

// --------------------------------------------------
// 9. VENDOR BILLS
// --------------------------------------------------
async function loadVendorBills() {
    const bills = await fetchAPI(`${API_BASE}/vendor-bills`);
    const tbody = document.getElementById("vb-tbody");
    tbody.innerHTML = "";
    if (bills) {
        bills.forEach(vb => {
            const isPaid = Boolean(vb.paid);
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${vb.id}</td>
                <td>${vb.vendorId}</td>
                <td>${vb.purchaseOrderId}</td>
                <td>${escapeHtml(vb.description || '')}</td>
                <td>₹${(vb.amount || 0).toFixed(2)}</td>
                <td>${vb.billDate || ''}</td>
                <td><span class="badge ${isPaid ? 'badge-success' : 'badge-danger'}">${isPaid ? 'PAID' : 'UNPAID'}</span></td>
                <td>
                    ${!isPaid ? `<button class="btn btn-pay btn-sm" onclick="payVendorBill(${vb.id})">Mark Paid</button>` : `<span class="badge badge-success">Completed</span>`}
                </td>
            `;
            tbody.appendChild(tr);
        });
    }
}

async function handleVendorBillSubmit(event) {
    event.preventDefault();
    const billData = {
        vendorId: parseInt(document.getElementById("vb-vendor-id").value),
        purchaseOrderId: parseInt(document.getElementById("vb-po-id").value),
        description: document.getElementById("vb-description").value,
        amount: parseFloat(document.getElementById("vb-amount").value),
        billDate: document.getElementById("vb-date").value || null,
        paid: false
    };

    console.log("Sending Vendor Bill:", billData);

    await fetchAPI(`${API_BASE}/vendor-bills`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(billData)
    });

    alert("Vendor bill saved!");
    document.getElementById("vendor-bill-form").reset();
    loadVendorBills();
}

async function payVendorBill(id) {
    if (confirm(`Mark Vendor Bill #${id} as paid? This will create payment and double-entry accounting records.`)) {
        console.log(`Processing Vendor Bill Payment for ID #${id}`);
        await fetchAPI(`${API_BASE}/vendor-bills/${id}/pay`, {
            method: "PUT"
        });
        alert(`Vendor Bill #${id} marked as paid successfully!`);
        loadVendorBills();
    }
}

// --------------------------------------------------
// 10. SALES ORDERS
// --------------------------------------------------
async function loadSalesOrders() {
    const orders = await fetchAPI(`${API_BASE}/sales-orders`);
    const tbody = document.getElementById("so-tbody");
    tbody.innerHTML = "";
    if (orders) {
        orders.forEach(so => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${so.id}</td>
                <td>${so.customerId}</td>
                <td>${so.buildingId}</td>
                <td>${so.serviceId}</td>
                <td>${so.quantity}</td>
                <td>₹${(so.rate || 0).toFixed(2)}</td>
                <td><strong>₹${(so.totalAmount || 0).toFixed(2)}</strong></td>
                <td>${so.orderDate || ''}</td>
                <td><span class="badge badge-info">${so.status}</span></td>
                <td>
                    <button class="btn btn-secondary btn-sm" onclick="editSO(${so.id}, ${so.customerId}, ${so.buildingId}, ${so.serviceId}, ${so.quantity}, ${so.rate}, ${so.totalAmount}, '${so.orderDate || ''}', '${so.status}')">Edit</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    }
}

async function handleSOSubmit(event) {
    event.preventDefault();
    const id = document.getElementById("so-id").value;
    const qty = parseInt(document.getElementById("so-quantity").value);
    const rate = parseFloat(document.getElementById("so-rate").value);
    const totalInput = document.getElementById("so-total").value;
    const totalAmount = totalInput ? parseFloat(totalInput) : (qty * rate);

    const soData = {
        customerId: parseInt(document.getElementById("so-customer-id").value),
        buildingId: parseInt(document.getElementById("so-building-id").value),
        serviceId: parseInt(document.getElementById("so-service-id").value),
        quantity: qty,
        rate: rate,
        totalAmount: totalAmount,
        orderDate: document.getElementById("so-date").value || null,
        status: document.getElementById("so-status").value
    };

    console.log("Sending Sales Order:", soData);

    const method = id ? "PUT" : "POST";
    const url = id ? `${API_BASE}/sales-orders/${id}` : `${API_BASE}/sales-orders`;

    await fetchAPI(url, {
        method: method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(soData)
    });

    alert(id ? "Sales Order updated!" : "Sales Order created!");
    resetSOForm();
    loadSalesOrders();
}

function editSO(id, custId, bldgId, svcId, qty, rate, total, date, status) {
    document.getElementById("so-id").value = id;
    document.getElementById("so-customer-id").value = custId;
    document.getElementById("so-building-id").value = bldgId;
    document.getElementById("so-service-id").value = svcId;
    document.getElementById("so-quantity").value = qty;
    document.getElementById("so-rate").value = rate;
    document.getElementById("so-total").value = total;
    document.getElementById("so-date").value = date;
    document.getElementById("so-status").value = status;
    document.getElementById("so-form-title").innerText = `Edit Sales Order #${id}`;
}

function resetSOForm() {
    document.getElementById("so-id").value = "";
    document.getElementById("so-form").reset();
    document.getElementById("so-form-title").innerText = "Create Sales Order";
}

// --------------------------------------------------
// 11. INVOICES
// --------------------------------------------------
async function loadInvoices() {
    const invoices = await fetchAPI(`${API_BASE}/invoices`);
    const tbody = document.getElementById("invoices-tbody");
    tbody.innerHTML = "";
    if (invoices) {
        invoices.forEach(inv => {
            const isPaid = Boolean(inv.paid);
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${inv.id}</td>
                <td>${inv.customerId}</td>
                <td>${inv.salesOrderId || ''}</td>
                <td>${escapeHtml(inv.description || '')}</td>
                <td>${inv.quantity || ''}</td>
                <td><strong>₹${(inv.amount || 0).toFixed(2)}</strong></td>
                <td>${inv.invoiceDate || ''}</td>
                <td><span class="badge ${isPaid ? 'badge-success' : 'badge-danger'}">${isPaid ? 'PAID' : 'UNPAID'}</span></td>
                <td>
                    ${!isPaid ? `<button class="btn btn-pay btn-sm" onclick="payInvoice(${inv.id})">Mark Paid</button>` : `<span class="badge badge-success">Completed</span>`}
                </td>
            `;
            tbody.appendChild(tr);
        });
    }
}

async function handleInvoiceSubmit(event) {
    event.preventDefault();
    const invoiceData = {
        customerId: parseInt(document.getElementById("inv-customer-id").value),
        salesOrderId: parseInt(document.getElementById("inv-so-id").value),
        description: document.getElementById("inv-description").value,
        quantity: parseInt(document.getElementById("inv-quantity").value) || null,
        amount: parseFloat(document.getElementById("inv-amount").value),
        invoiceDate: document.getElementById("inv-date").value || null,
        paid: false
    };

    console.log("Sending Invoice:", invoiceData);

    await fetchAPI(`${API_BASE}/invoices`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(invoiceData)
    });

    alert("Invoice generated!");
    document.getElementById("invoice-form").reset();
    loadInvoices();
}

async function payInvoice(id) {
    if (confirm(`Mark Invoice #${id} as paid? This will record customer payment and double-entry journal entries.`)) {
        console.log(`Processing Invoice Payment for ID #${id}`);
        await fetchAPI(`${API_BASE}/invoices/${id}/pay`, {
            method: "PUT"
        });
        alert(`Invoice #${id} marked as paid successfully!`);
        loadInvoices();
    }
}

// --------------------------------------------------
// 12. PAYMENTS
// --------------------------------------------------
async function loadPayments() {
    const payments = await fetchAPI(`${API_BASE}/payments`);
    const tbody = document.getElementById("payments-tbody");
    tbody.innerHTML = "";
    if (payments) {
        payments.forEach(p => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${p.id}</td>
                <td>${p.contactId}</td>
                <td><span class="badge ${p.paymentType === 'RECEIVED' ? 'badge-success' : 'badge-info'}">${p.paymentType}</span></td>
                <td>${p.referenceType}</td>
                <td>${p.referenceId}</td>
                <td><strong>₹${(p.amount || 0).toFixed(2)}</strong></td>
                <td>${p.paymentDate || ''}</td>
                <td>${p.paymentMethod || ''}</td>
            `;
            tbody.appendChild(tr);
        });
    }
}

async function handlePaymentSubmit(event) {
    event.preventDefault();
    const payData = {
        contactId: parseInt(document.getElementById("pay-contact-id").value),
        paymentType: document.getElementById("pay-type").value,
        referenceType: document.getElementById("pay-ref-type").value,
        referenceId: parseInt(document.getElementById("pay-ref-id").value),
        amount: parseFloat(document.getElementById("pay-amount").value),
        paymentDate: document.getElementById("pay-date").value || null,
        paymentMethod: document.getElementById("pay-method").value
    };

    console.log("Sending Payment:", payData);

    await fetchAPI(`${API_BASE}/payments`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payData)
    });

    alert("Payment recorded!");
    document.getElementById("payment-form").reset();
    loadPayments();
}

// --------------------------------------------------
// 13. ACCOUNTS
// --------------------------------------------------
async function loadAccounts() {
    const accounts = await fetchAPI(`${API_BASE}/accounts`);
    const tbody = document.getElementById("accounts-tbody");
    tbody.innerHTML = "";
    if (accounts) {
        accounts.forEach(acc => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${acc.id}</td>
                <td><code>${escapeHtml(acc.accountCode)}</code></td>
                <td><strong>${escapeHtml(acc.accountName)}</strong></td>
                <td><span class="badge badge-info">${acc.accountType}</span></td>
                <td>₹${(acc.balance || 0).toFixed(2)}</td>
                <td>
                    <button class="btn btn-secondary btn-sm" onclick="editAccount(${acc.id}, '${escapeHtml(acc.accountCode)}', '${escapeHtml(acc.accountName)}', '${acc.accountType}', ${acc.balance})">Edit</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    }
}

async function handleAccountSubmit(event) {
    event.preventDefault();
    const id = document.getElementById("account-id").value;
    const accData = {
        accountCode: document.getElementById("acc-code").value,
        accountName: document.getElementById("acc-name").value,
        accountType: document.getElementById("acc-type").value,
        balance: parseFloat(document.getElementById("acc-balance").value) || 0.0
    };

    console.log("Sending Account:", accData);

    const method = id ? "PUT" : "POST";
    const url = id ? `${API_BASE}/accounts/${id}` : `${API_BASE}/accounts`;

    await fetchAPI(url, {
        method: method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(accData)
    });

    alert(id ? "Account updated!" : "Account created!");
    resetAccountForm();
    loadAccounts();
}

function editAccount(id, code, name, type, balance) {
    document.getElementById("account-id").value = id;
    document.getElementById("acc-code").value = code;
    document.getElementById("acc-name").value = name;
    document.getElementById("acc-type").value = type;
    document.getElementById("acc-balance").value = balance;
    document.getElementById("account-form-title").innerText = `Edit Account #${id}`;
}

function resetAccountForm() {
    document.getElementById("account-id").value = "";
    document.getElementById("account-form").reset();
    document.getElementById("account-form-title").innerText = "Add New Account";
}

// --------------------------------------------------
// 14. JOURNAL ENTRIES
// --------------------------------------------------
async function loadJournalEntries() {
    const entries = await fetchAPI(`${API_BASE}/journal-entries`);
    const tbody = document.getElementById("je-tbody");
    tbody.innerHTML = "";
    if (entries) {
        entries.forEach(je => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${je.id}</td>
                <td>${je.transactionDate || ''}</td>
                <td>${escapeHtml(je.description || '')}</td>
                <td>Debit Account #${je.debitAccountId}</td>
                <td>Credit Account #${je.creditAccountId}</td>
                <td><strong>₹${(je.amount || 0).toFixed(2)}</strong></td>
            `;
            tbody.appendChild(tr);
        });
    }
}

async function handleJESubmit(event) {
    event.preventDefault();
    const jeData = {
        transactionDate: document.getElementById("je-date").value || null,
        description: document.getElementById("je-description").value,
        debitAccountId: parseInt(document.getElementById("je-debit-id").value),
        creditAccountId: parseInt(document.getElementById("je-credit-id").value),
        amount: parseFloat(document.getElementById("je-amount").value)
    };

    console.log("Sending Journal Entry:", jeData);

    await fetchAPI(`${API_BASE}/journal-entries`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(jeData)
    });

    alert("Double-entry journal transaction recorded!");
    document.getElementById("je-form").reset();
    loadJournalEntries();
}

// --------------------------------------------------
// 15. BUDGETS
// --------------------------------------------------
async function loadBudgets() {
    const budgets = await fetchAPI(`${API_BASE}/budgets`);
    const tbody = document.getElementById("budgets-tbody");
    tbody.innerHTML = "";
    if (budgets) {
        budgets.forEach(b => {
            const planned = b.plannedAmount || 0;
            const actual = b.actualAmount || 0;
            const remaining = planned - actual;

            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${b.id}</td>
                <td>Zone #${b.zoneId}</td>
                <td>${escapeHtml(b.analyticAccount || '')}</td>
                <td>${escapeHtml(b.periodName || '')}</td>
                <td>₹${planned.toFixed(2)}</td>
                <td>₹${actual.toFixed(2)}</td>
                <td><strong style="color: ${remaining >= 0 ? '#15803d' : '#b91c1c'};">₹${remaining.toFixed(2)}</strong></td>
                <td>
                    <button class="btn btn-secondary btn-sm" onclick="editBudget(${b.id}, ${b.zoneId}, '${escapeHtml(b.analyticAccount || '')}', '${escapeHtml(b.periodName || '')}', ${planned}, ${actual})">Edit</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    }
}

async function handleBudgetSubmit(event) {
    event.preventDefault();
    const id = document.getElementById("budget-id").value;
    const bgtData = {
        zoneId: parseInt(document.getElementById("bgt-zone-id").value),
        analyticAccount: document.getElementById("bgt-analytic").value,
        periodName: document.getElementById("bgt-period").value,
        plannedAmount: parseFloat(document.getElementById("bgt-planned").value),
        actualAmount: parseFloat(document.getElementById("bgt-actual").value) || 0.0
    };

    console.log("Sending Budget:", bgtData);

    const method = id ? "PUT" : "POST";
    const url = id ? `${API_BASE}/budgets/${id}` : `${API_BASE}/budgets`;

    await fetchAPI(url, {
        method: method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(bgtData)
    });

    alert(id ? "Budget updated!" : "Budget set!");
    resetBudgetForm();
    loadBudgets();
}

function editBudget(id, zoneId, analytic, period, planned, actual) {
    document.getElementById("budget-id").value = id;
    document.getElementById("bgt-zone-id").value = zoneId;
    document.getElementById("bgt-analytic").value = analytic;
    document.getElementById("bgt-period").value = period;
    document.getElementById("bgt-planned").value = planned;
    document.getElementById("bgt-actual").value = actual;
    document.getElementById("budget-form-title").innerText = `Edit Budget #${id}`;
}

function resetBudgetForm() {
    document.getElementById("budget-id").value = "";
    document.getElementById("budget-form").reset();
    document.getElementById("budget-form-title").innerText = "Create / Set Zone Budget";
}

// --------------------------------------------------
// 16. REPORTS (JdbcTemplate Endpoints)
// --------------------------------------------------
async function loadPnlReport() {
    const data = await fetchAPI(`${API_BASE}/reports/pnl`);
    if (data) {
        document.getElementById("pnl-revenue").innerText = `₹${(data.totalFertilizerRevenue || 0).toLocaleString('en-IN', {minimumFractionDigits: 2})}`;
        document.getElementById("pnl-expenses").innerText = `₹${(data.totalOperatingExpenses || 0).toLocaleString('en-IN', {minimumFractionDigits: 2})}`;
        
        const net = data.netProfit || 0;
        const profitEl = document.getElementById("pnl-profit");
        profitEl.innerText = `₹${net.toLocaleString('en-IN', {minimumFractionDigits: 2})}`;
        profitEl.style.color = net >= 0 ? "#15803d" : "#b91c1c";

        document.getElementById("pnl-results").style.display = "block";
    }
}

async function loadBalanceSheetReport() {
    const data = await fetchAPI(`${API_BASE}/reports/balance-sheet`);
    if (data) {
        document.getElementById("bs-liabilities").innerText = `₹${(data.openVendorLiabilities || 0).toLocaleString('en-IN', {minimumFractionDigits: 2})}`;
        document.getElementById("bs-cash").innerText = `₹${(data.totalBankAndCash || 0).toLocaleString('en-IN', {minimumFractionDigits: 2})}`;
        document.getElementById("bs-results").style.display = "block";
    }
}

// Utility: HTML Sanitizer
function escapeHtml(text) {
    if (!text) return "";
    return String(text)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

// Utility: DateTime Formatter
function formatDateTime(dtStr) {
    if (!dtStr) return "";
    try {
        const d = new Date(dtStr);
        return d.toLocaleString();
    } catch(e) {
        return dtStr;
    }
}
