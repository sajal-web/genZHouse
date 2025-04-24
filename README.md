# Room Rent Service Application

## Introduction
The **Room Rent Service Application** is a platform that connects room owners, brokers, and users looking for rental accommodations. It allows owners to list rooms, brokers to manage multiple listings through a paid model, and users to search, book, and verify rooms online.

## Features
- **User Authentication:** Secure login/signup using OTP verification.
- **Room Listing:** Owners can list up to 5 rooms for free; additional listings require a broker ID.
- **Broker Management:** Brokers can list rooms under a paid plan model.
- **Room Booking:** Users can browse, filter, and book rooms online.
- **Admin Panel:** Admins can manage users, brokers, and room listings.
- **Payment Integration:** For paid broker plans and advanced features.

## Tech Stack
### **Frontend**
- **Android:** Kotlin (Native) + XML (UI Design)
- **iOS:** Swift (Native) + UIKit

### **Backend**
- **Server-side:** Node.js, Express.js
- **Database:** MySQL with Sequelize ORM
- **Authentication:** JWT-based authentication

### **Admin Panel**
- Web-based dashboard (Technology TBD: React.js/Vue.js/Angular)

## Installation
### **Backend Setup**
1. Clone the repository:
   ```sh
   git clone https://github.com/yourusername/room-rent-service.git
   cd room-rent-service
   ```
2. Install dependencies:
   ```sh
   npm install
   ```
3. Set up the environment variables in a `.env` file:
   ```env
   DB_HOST=your_database_host
   DB_USER=your_database_user
   DB_PASS=your_database_password
   JWT_SECRET=your_secret_key
   ```
4. Run database migrations:
   ```sh
   npx sequelize-cli db:migrate
   ```
5. Start the backend server:
   ```sh
   npm start
   ```

### **Frontend Setup (Android & iOS)**
1. Open the respective projects in **Android Studio** (for Kotlin) or **Xcode** (for Swift).
2. Install dependencies.
3. Connect to the backend by updating the API endpoints in the code.
4. Run the application on an emulator or a real device.

## API Documentation
(To be added – Provide details on available API endpoints and request/response structures.)

## Contribution
1. Fork the repository.
2. Create a new branch (`feature/your-feature-name`).
3. Commit your changes.
4. Push to your forked repository and create a pull request.

## License
This project is licensed under the MIT License.

## Contact
For any inquiries, feel free to reach out to mahatosajal65@gmail.com

