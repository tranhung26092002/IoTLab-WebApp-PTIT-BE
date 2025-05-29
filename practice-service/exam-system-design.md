# Thiết Kế Hệ Thống Bài Kiểm Tra

## 1. Yêu Cầu Hệ Thống

### 1.1. Ngân Hàng Câu Hỏi

- Hai loại câu hỏi:
  1. Trắc nghiệm:
     - Nội dung câu hỏi
     - 4 đáp án (A, B, C, D)
     - 1 đáp án đúng
  2. Tự luận:
     - Chỉ có nội dung câu hỏi

### 1.2. Cấu Trúc Bài Kiểm Tra

- Mỗi bài kiểm tra gồm:
  - 20 câu hỏi trắc nghiệm
  - 1 câu hỏi tự luận
- Mỗi sinh viên được phân một mã đề riêng

### 1.3. Quy Trình Làm Bài

1. Sinh viên nhận mã đề
2. Trả lời câu hỏi:
   - Trắc nghiệm: Chọn 1 đáp án đúng nhất
   - Tự luận: 
     - Trả lời bằng văn bản
     - Tối đa 3 ảnh kèm theo
3. Nộp bài (Submit):
   - Hiển thị đáp án phần trắc nghiệm ngay
   - Phần tự luận chờ giáo viên chấm

## 2. Thiết Kế Kỹ Thuật

### 2.1. Công Nghệ Sử Dụng

- **Backend Framework**: Spring Boot
- **Database**: PostgreSQL
- **ORM**: JPA/Hibernate
- **Spring Data JPA**: Cho các thao tác CRUD cơ bản
- **Spring Security**: Xác thực và phân quyền
- **Spring Validation**: Kiểm tra dữ liệu đầu vào

### 2.2. Cấu Trúc Entity

#### Question (Câu hỏi)

```java
@Entity
@Table(name = "question_bank")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private QuestionType type; // MULTIPLE_CHOICE/ESSAY

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MultipleChoiceOption> options;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

#### MultipleChoiceOption (Đáp án trắc nghiệm)

```java
@Entity
@Table(name = "multiple_choice_options")
public class MultipleChoiceOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    private String option; // A, B, C, D

    @Column(columnDefinition = "TEXT")
    private String content;

    private boolean isCorrect;
}
```

#### StudentAnswer (Câu trả lời của sinh viên)

```java
@Entity
@Table(name = "student_answer")
public class StudentAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_exam_id")
    private StudentExam studentExam;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(columnDefinition = "TEXT")
    private String answer; // Cho câu tự luận

    private String selectedOption; // Cho câu trắc nghiệm (A, B, C, D)

    @Column(name = "image_urls", columnDefinition = "jsonb")
    private String imageUrls; // JSON array của URLs ảnh cho câu tự luận

    private Double score;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

#### Exam (Bài kiểm tra)

```java
@Entity
@Table(name = "exam")
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL)
    private List<ExamQuestion> questions;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

### 2.3. Repository Pattern

#### QuestionRepository

```java
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByType(QuestionType type);

    @Query("SELECT q FROM Question q WHERE q.type = :type ORDER BY RANDOM() LIMIT :limit")
    List<Question> findRandomQuestionsByType(@Param("type") QuestionType type, @Param("limit") int limit);
}
```

#### StudentAnswerRepository

```java
@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {
    List<StudentAnswer> findByStudentExamId(Long studentExamId);
}
```

## 3. API Endpoints

### 3.1. Quản Lý Ngân Hàng Câu Hỏi

- `POST /api/questions/multiple-choice`: Tạo câu hỏi trắc nghiệm
- `POST /api/questions/essay`: Tạo câu hỏi tự luận
- `GET /api/questions`: Lấy danh sách câu hỏi
- `GET /api/questions/{id}`: Lấy chi tiết câu hỏi
- `PUT /api/questions/{id}`: Cập nhật câu hỏi
- `DELETE /api/questions/{id}`: Xóa câu hỏi

### 3.2. Quản Lý Bài Kiểm Tra

- `POST /api/exams`: Tạo bài kiểm tra mới
- `GET /api/exams`: Lấy danh sách bài kiểm tra
- `GET /api/exams/{id}`: Lấy chi tiết bài kiểm tra
- `GET /api/exams/{id}/student`: Lấy mã đề cho sinh viên

### 3.3. Quản Lý Bài Làm Của Sinh Viên

- `POST /api/student-exams/{id}/answers`: Lưu câu trả lời
  - Trắc nghiệm: `selectedOption`
  - Tự luận: `answer` và `images`
- `POST /api/student-exams/{id}/submit`: Nộp bài
- `GET /api/student-exams/{id}/results`: Xem kết quả
  - Hiển thị đáp án trắc nghiệm
  - Hiển thị trạng thái chấm điểm tự luận

## 4. Quy Trình Hoạt Động

### 4.1. Quy Trình Tạo Bài Kiểm Tra

1. Tạo câu hỏi trong ngân hàng câu hỏi
2. Tạo bài kiểm tra mới
3. Hệ thống tự động chọn:
   - 20 câu trắc nghiệm ngẫu nhiên
   - 1 câu tự luận ngẫu nhiên
4. Lưu thông tin bài kiểm tra

### 4.2. Quy Trình Làm Bài Của Sinh Viên

1. Sinh viên nhận mã đề
2. Trả lời từng câu hỏi:
   - Trắc nghiệm: Chọn 1 đáp án
   - Tự luận: Nhập câu trả lời và upload ảnh (tối đa 3 ảnh)
3. Nộp bài:
   - Hệ thống chấm điểm tự động phần trắc nghiệm
   - Hiển thị đáp án trắc nghiệm
   - Phần tự luận chờ giáo viên chấm

### 4.3. Quy Trình Chấm Điểm

1. Phần trắc nghiệm:
   - Chấm tự động khi nộp bài
   - Hiển thị đáp án ngay
2. Phần tự luận:
   - Giáo viên chấm điểm
   - Cập nhật tổng điểm bài thi

## 5. Tiến Độ Triển Khai

### 5.1. Giai Đoạn 1: Thiết Lập Cơ Sở Dữ Liệu

- [x] Thiết kế cơ sở dữ liệu
- [x] Tạo các entity classes
- [x] Cấu hình JPA/Hibernate
- [x] Tạo các repository interfaces

### 5.2. Giai Đoạn 2: Xây Dựng API Cơ Bản

- [x] Tạo các DTO classes
- [x] Implement các repository methods
- [x] Tạo các service classes
- [x] Xây dựng các REST controllers
- [x] Cấu hình security và validation

### 5.3. Giai Đoạn 3: Xây Dựng Logic Nghiệp Vụ

- [x] Implement logic tạo câu hỏi
- [x] Implement logic tạo bài kiểm tra
- [x] Implement logic chấm điểm
- [x] Implement logic upload/download hình ảnh

### 5.5. Giai Đoạn 5: Kiểm Thử và Tối Ưu

- [x] Viết unit tests
- [x] Viết integration tests
- [x] Tối ưu hóa queries
- [x] Kiểm thử hiệu năng
