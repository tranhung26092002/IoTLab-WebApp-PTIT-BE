# Thiết Kế Hệ Thống Bài Kiểm Tra

## 1. Yêu Cầu Hệ Thống

### 1.1. Cấu Trúc Bài Kiểm Tra

- Mỗi bài kiểm tra gồm 2 phần:
  - Phần trắc nghiệm: 20 câu hỏi (mỗi câu 4 đáp án)
  - Phần tự luận: 1 câu hỏi lý thuyết
    - Yêu cầu trả lời bằng đoạn văn bản
    - Kèm theo tối đa 3 hình ảnh

### 1.2. Ngân Hàng Câu Hỏi

- Hai loại câu hỏi:
  1. Trắc nghiệm:
     - 1 câu hỏi
     - 4 đáp án (A, B, C, D)
     - 1 đáp án đúng
  2. Tự luận:
     - 1 câu hỏi lý thuyết
     - Đáp án gồm:
       - 1 đoạn văn bản
       - Tối đa 3 hình ảnh

## 2. Thiết Kế Kỹ Thuật

### 2.1. Công Nghệ Sử Dụng

- **Backend Framework**: Spring Boot
- **Database**: PostgreSQL
- **ORM**: JPA/Hibernate
- **JDBC**: Cho các truy vấn phức tạp
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
    private QuestionType type; // TRAC_NGHIEM/TU_LUAN

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<MultipleChoiceOption> options;

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL)
    private EssayAnswer essayAnswer;

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

#### EssayAnswer (Đáp án tự luận)

```java
@Entity
@Table(name = "essay_answer")
public class EssayAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(columnDefinition = "TEXT")
    private String answerText;

    @Column(columnDefinition = "jsonb")
    private List<String> imageUrls;
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

#### ExamRepository

```java
@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByStatus(ExamStatus status);
}
```

### 2.4. JDBC Template cho Truy Vấn Phức Tạp

```java
@Repository
public class ExamCustomRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<StudentExamResult> getStudentExamResults(Long examId) {
        String sql = """
            SELECT se.id, se.student_id, se.score,
                   COUNT(CASE WHEN sa.score > 0 THEN 1 END) as correct_answers
            FROM student_exam se
            LEFT JOIN student_answer sa ON se.id = sa.student_exam_id
            WHERE se.exam_id = ?
            GROUP BY se.id, se.student_id, se.score
            """;

        return jdbcTemplate.query(sql, new Object[]{examId}, (rs, rowNum) ->
            new StudentExamResult(
                rs.getLong("id"),
                rs.getLong("student_id"),
                rs.getDouble("score"),
                rs.getInt("correct_answers")
            )
        );
    }
}
```

## 3. API Endpoints

### 3.1. Quản Lý Ngân Hàng Câu Hỏi

- `POST /api/questions`: Tạo câu hỏi mới
- `GET /api/questions`: Lấy danh sách câu hỏi
- `GET /api/questions/{id}`: Lấy chi tiết câu hỏi
- `PUT /api/questions/{id}`: Cập nhật câu hỏi
- `DELETE /api/questions/{id}`: Xóa câu hỏi

### 3.2. Quản Lý Bài Kiểm Tra

- `POST /api/exams`: Tạo bài kiểm tra mới
- `GET /api/exams`: Lấy danh sách bài kiểm tra
- `GET /api/exams/{id}`: Lấy chi tiết bài kiểm tra
- `PUT /api/exams/{id}`: Cập nhật bài kiểm tra
- `DELETE /api/exams/{id}`: Xóa bài kiểm tra

### 3.3. Quản Lý Bài Làm Của Sinh Viên

- `POST /api/student-exams`: Bắt đầu làm bài
- `GET /api/student-exams/{id}`: Lấy thông tin bài làm
- `PUT /api/student-exams/{id}/submit`: Nộp bài
- `GET /api/student-exams/{id}/answers`: Lấy câu trả lời
- `POST /api/student-exams/{id}/answers`: Lưu câu trả lời

## 4. Quy Trình Hoạt Động

### 4.1. Quy Trình Tạo Bài Kiểm Tra

1. Tạo câu hỏi trong ngân hàng câu hỏi
2. Tạo bài kiểm tra mới
3. Chọn ngẫu nhiên 20 câu trắc nghiệm và 1 câu tự luận từ ngân hàng câu hỏi
4. Lưu thông tin bài kiểm tra và các câu hỏi được chọn

### 4.2. Quy Trình Làm Bài Của Sinh Viên

1. Sinh viên bắt đầu làm bài
2. Hệ thống tạo bản ghi Student Exam
3. Sinh viên trả lời từng câu hỏi
4. Hệ thống lưu câu trả lời vào Student Answer
5. Sinh viên nộp bài
6. Hệ thống chấm điểm tự động cho phần trắc nghiệm
7. Giáo viên chấm điểm phần tự luận

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
