![WhatsApp Image 2025-05-20 at 22 53 57_db367304](https://github.com/user-attachments/assets/01241205-06e7-4287-ab06-a34fd6951a22)
![WhatsApp Image 2025-05-20 at 22 53 57_2e7dff9e](https://github.com/user-attachments/assets/5c29e2ea-6db5-42d9-8293-c87336b0e129)
![WhatsApp Image 2025-05-20 at 22 53 58_5680a1ef](https://github.com/user-attachments/assets/ec846cf6-1962-4681-9ccc-d14f8a25bb9b)
![WhatsApp Image 2025-05-20 at 22 53 58_07b20f56](https://github.com/user-attachments/assets/b4ff798f-e330-4a9e-bb90-035031724adc)
![WhatsApp Image 2025-05-20 at 22 53 58_dc04a90e](https://github.com/user-attachments/assets/a02d3730-2f4e-4b3c-93f2-f283f32e7d04)
🛠️ VirtuDecor Admin App
VirtuDecor Admin App is the admin-side Android application of the VirtuDecor platform — an Augmented Reality (AR) based Furniture Store that allows users to visualize furniture in their home before buying. This admin app allows the store admin to manage furniture products, upload 3D AR models, view orders, and track total earnings.

🚀 Features
📦 Add Furniture Items
Admins can add new furniture products along with descriptions, prices, images, and AR models in .glb format.

📁 Upload 3D Models
Upload .glb files to be rendered in the AR section of the customer app using Sceneform + ARCore.

📊 View Orders
View all user orders with their status (Pending, Completed).

📈 Track Earnings
See total sales/earnings from completed orders.

🧰 Tech Stack
Jetpack Compose – Modern UI toolkit for native Android

Firebase – Authentication, Realtime Database, and Firestore

Sceneform + ARCore + Filament – For 3D model rendering in the customer app

Razorpay – Secure payment gateway integration

📁 Folder Structure
VirtuDecor-Admin/
├── app/
│ ├── src/
│ │ ├── main/
│ │ │ ├── java/com/virtudecor/admin/
│ │ │ │ ├── ui/ # Jetpack Compose Screens
│ │ │ │ ├── viewmodel/ # ViewModels
│ │ │ │ ├── data/ # Firebase DB functions
│ │ │ │ └── model/ # Data classes
│ │ │ └── res/ # Icons, themes
│ └── build.gradle
├── build.gradle
└── README.md

📝 How to Use
Clone the Repository

bash
Copy
Edit
git clone https://github.com/531ra/AdminVirtuDecor.git
cd virtudecor-admin
Open in Android Studio
Open the project in Android Studio and sync Gradle.

Set Up Firebase

Add your google-services.json to the app/ directory

Enable Firebase Authentication (Email/Password)

Create Firestore and Realtime Database structures:

/furniture/ – for furniture data

/orders/ – for tracking orders

/earnings/ – to track total income

Upload AR Models
Upload .glb files to Firebase Storage or any URL-accessible location and link them in the product data.

🔐 Admin Access
Only authorized users (admins) can log in and access this app. Use Firebase security rules to restrict access to admin users only.

🤝 Contributing
Want to contribute or suggest improvements?
Open an issue or submit a pull request – contributions are welcome!

📃 License
This project is licensed under the MIT License.

🙋‍♂️ Author
Raghav 
Android Developer | BTech Graduate | Passionate about AR and tech innovation
LinkedIn ->https://www.linkedin.com/in/raghav-anand531


