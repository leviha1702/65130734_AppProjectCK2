🦵 KneeCare – Ứng dụng hỗ trợ phục hồi đầu gối

📌 Giới thiệu

KneeCare là ứng dụng Android hỗ trợ người dùng theo dõi tình trạng đầu gối và nhận gợi ý bài tập phục hồi phù hợp. Ứng dụng hướng đến người chơi thể thao như bóng đá, chạy bộ hoặc những người gặp vấn đề về đau gối, lỏng khớp.

Phiên bản hiện tại đã xây dựng thành công luồng hoạt động cơ bản: nhập dữ liệu → phân tích → hiển thị kết quả.

🎯 Mục tiêu

Hỗ trợ người dùng đánh giá tình trạng đầu gối
Đưa ra gợi ý tập luyện phù hợp
Xây dựng nền tảng để tích hợp AI và Mobile ML
Phát triển theo hướng ứng dụng thực tế trong chăm sóc sức khỏe

⚙️ Công nghệ sử dụng

Ngôn ngữ: Java
IDE: Android Studio
UI: XML (Material Design cơ bản)
Kiến trúc: Activity-based
AI (hiện tại): Rule-based (logic điều kiện)

📱 Chức năng hiện tại (Giai đoạn 1)

Trang mới vào app 

<img width="282" height="666" alt="Ảnh màn hình 2026-05-26 lúc 00 46 18" src="https://github.com/user-attachments/assets/21bd48f7-95b9-4633-bc92-31f3941ee663" />


Nhập tình trạng đầu gối (mức độ đau, triệu chứng)

<img width="296" height="310" alt="Ảnh màn hình 2026-05-26 lúc 00 49 05" src="https://github.com/user-attachments/assets/9d6692b0-92b7-42f0-9af5-cb3153c927d7" />


Phân tích dữ liệu đầu vào và hiển thị kết quả

<img width="298" height="253" alt="Ảnh màn hình 2026-05-26 lúc 00 49 51" src="https://github.com/user-attachments/assets/56133081-e3df-4635-9850-f938e8ab021c" />

Trang đăng nhập và đăng ký:

<img width="299" height="365" alt="Ảnh màn hình 2026-05-27 lúc 14 02 06" src="https://github.com/user-attachments/assets/d2db29b6-d84b-4a81-a186-10239911b179" />
<img width="299" height="402" alt="Ảnh màn hình 2026-05-27 lúc 13 38 40" src="https://github.com/user-attachments/assets/08694b00-4262-4e45-bfb0-0fc248a92b9b" />

Giai đoạn 2:

Trang chủ:        <img width="174" height="387" alt="Ảnh màn hình 2026-05-27 lúc 16 17 40" src="https://github.com/user-attachments/assets/5d29fd0b-a066-4d1d-ab39-74339c5cca45" />


Trang cài đặt:         <img width="175" height="386" alt="Ảnh màn hình 2026-05-27 lúc 16 21 56" src="https://github.com/user-attachments/assets/743bfa14-094f-4e2f-9446-f0b5c7df21b0" />

Giai đoạn 3:

Trang lịch sử: 

<img width="173" height="384" alt="Ảnh màn hình 2026-05-29 lúc 01 41 52" src="https://github.com/user-attachments/assets/3887a85d-564c-4db0-bdc6-d63780249639" />



Trang chatbot: 

<img width="175" height="387" alt="Ảnh màn hình 2026-05-28 lúc 13 42 03" src="https://github.com/user-attachments/assets/265d7fd9-6132-4d12-87dc-7b9731d4dd19" />


Trang lịch tập hồi phục từ phân tích kết quả AI cho thấy:

<img width="173" height="384" alt="Ảnh màn hình 2026-05-29 lúc 01 41 52" src="https://github.com/user-attachments/assets/3a09082a-5adb-481c-9ba8-ec0457fd89b3" />
<img width="175" height="386" alt="Ảnh màn hình 2026-05-29 lúc 01 41 30" src="https://github.com/user-attachments/assets/dba6f4fe-a1bc-4d7a-9505-1acd03a47d3d" />

☁️ Đồng bộ và Lưu trữ Dữ liệu Đám mây (Google Cloud Firebase)

Ứng dụng triển khai kiến trúc **Client-to-Cloud** kết nối trực tiếp đến cơ sở dữ liệu NoSQL **Google Cloud Firestore**. Ngay sau khi bộ não AI (Google Gemini 3.5 Flash) xử lý xong phác đồ điều trị, toàn bộ dữ liệu định danh, triệu chứng lâm sàng và lịch trình tập luyện sẽ được đồng bộ theo thời gian thực (Real-time) lên hệ thống Server đám mây.

Hình ảnh thực tế trên Firebase Console:

<img width="1439" height="776" alt="Ảnh màn hình 2026-05-30 lúc 12 48 51" src="https://github.com/user-attachments/assets/12b25973-fb50-4a3a-afe2-0babfe53cc36" />

