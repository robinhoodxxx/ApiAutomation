[Api Automation.drawio.pdf](https://github.com/user-attachments/files/19525053/Api.Automation.drawio.pdf)
# 🧪 Service Automation Framework

A **modular**, **data-driven**, and **scalable** test automation framework focused on **service-level testing**, especially for **REST APIs**. Built to support rapid test configuration through Excel and generate clear, professional reports.

---
#How to run 
read the HowToRunInstruct.txt in project repo
in ide run through main class src/main/java/Runner/Orchestrator.java


## 📌 Scope

- Designed for REST API testing (JSON)
- Excel-driven test data and service configuration
- Scalable to support other services like Kafka
- Supports DB validation and rich reporting

---

## 🎯 Objective

Build a reusable and extensible automation framework that enables:

- Simple onboarding of new services via config
- Scalable, data-driven test execution
- Clear separation of test data, logic, and validation
- Minimal code changes for new test scenarios

---

## 🧩 Architecture & Workflow

> See full architecture: `Api Automation.drawio.pdf`

### 🛠️ Workflow Overview:
1. **Test Data Loader** - Reads `.xlsx` files for test parameters
2. **DB Validator** - Executes pre/post queries and checks expected DB state
3. **Service Executor** - Triggers REST APIs with dynamic payloads
4. **Payload Generator** - Assembles payloads from Excel test data
5. **Response Validator** - Verifies status code, headers, body, and schema
6. **Report Generator** - Produces HTML and Excel reports

---

## 🚀 Getting Started

### ✅ Prerequisites
- Java 11+
- Maven 3.6+
- Excel-compatible test case files
- Database access (if DB validations used)

### ✅ Clone the Repository
```bash
git clone https://github.com/robinhoodxxx/ApiAutomation.git
cd ApiAutomation
