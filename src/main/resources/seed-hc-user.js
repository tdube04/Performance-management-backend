// MongoDB Seed Script for HC User and HC Group
// Run this in MongoDB shell or use with mongosh

// ===============================
// Step 1: Create HC User Group
// ===============================
var hcGroup = {
    "_id": "HC",
    "name": "HC",
    "permissions": [
        "GET_ALL_USERS",
        "SEARCH_USER",
        "GET_USER",
        "SEARCH_USER_BY_GRADE",
        "SEARCH_USER_BY_DIVISION",
        "GET_HC_DASHBOARD",
        "MANAGE_NOTIFICATIONS",
        "GET_NOTIFICATIONS",
        "CREATE_NOTIFICATION",
        "UPDATE_NOTIFICATION",
        "DELETE_NOTIFICATION"
    ],
    "_class": "com.innovation.workplan.CollectionModels.UserGroup"
};

db.getCollection('user_groups').insertOne(hcGroup);
print("HC User Group created successfully!");

// ===============================
// Step 2: Create HC User
// ===============================
var hcUser = {
    "_id": "hcadmin",
    "username": "hcadmin",
    "password": "hcpass123",
    "email": "hc@zimra.co.zw",
    "name": "Human Capital",
    "surname": "Administrator",
    "ec_number": "HC001",
    "grade": "1",
    "positionName": "HC Manager",
    "divisionName": "Human Capital",
    "sectionName": "HC Administration",
    "userRole": ["HC", "USER"],
    "logAs": "hc",
    "enabled": true,
    "appraiser_status": "UnAssigned",
    "appraisees": [],
    "appraiserEmail": null,
    "token_status": null,
    "resetToken": null,
    "reset_token_expiry_date": null,
    "signature": null,
    "signatureStatus": null,
    "_class": "com.innovation.workplan.CollectionModels.UserEntity"
};

// Insert HC User into the correct collection
db.getCollection('users_tbl').insertOne(hcUser);

print("HC User created successfully!");
print("========================================");
print("CREDENTIALS:");
print("========================================");
print("Username: hcadmin");
print("Password: hcpass123");
print("Role: HC (Human Capital)");
print("");
print("After inserting, restart the backend and login with:");
print("Username: hcadmin");
print("Password: hcpass123");
