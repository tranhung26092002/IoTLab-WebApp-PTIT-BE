# Tổng Hợp Triển Khai Hệ Thống Bài Kiểm Tra

## 1. Entities

### 1.1. Question

**File**: `src/main/java/com/ptit/service/entity/Question.java`
**Mục đích**: Đại diện cho câu hỏi trong ngân hàng câu hỏi
**Các trường chính**:

- `id`: ID của câu hỏi
- `type`: Loại câu hỏi (TRAC_NGHIEM/TU_LUAN)
- `content`: Nội dung câu hỏi
- `options`: Danh sách đáp án (cho câu trắc nghiệm)
- `essayAnswer`: Đáp án (cho câu tự luận)
- `createdAt`: Thời gian tạo
- `updatedAt`: Thời gian cập nhật

### 1.2. MultipleChoiceOption

**File**: `src/main/java/com/ptit/service/entity/MultipleChoiceOption.java`
**Mục đích**: Đại diện cho đáp án trắc nghiệm
**Các trường chính**:

- `id`: ID của đáp án
- `question`: Câu hỏi liên kết
- `option`: Mã đáp án (A, B, C, D)
- `content`: Nội dung đáp án
- `isCorrect`: Đánh dấu đáp án đúng

### 1.3. EssayAnswer

**File**: `src/main/java/com/ptit/service/entity/EssayAnswer.java`
**Mục đích**: Đại diện cho đáp án tự luận
**Các trường chính**:

- `id`: ID của đáp án
- `question`: Câu hỏi liên kết
- `answerText`: Nội dung đáp án
- `imageUrls`: Danh sách URL hình ảnh

### 1.4. Exam

**File**: `src/main/java/com/ptit/service/entity/Exam.java`
**Mục đích**: Đại diện cho đề thi
**Các trường chính**:

- `id`: ID của đề thi
- `title`: Tiêu đề đề thi
- `description`: Mô tả đề thi
- `questions`: Danh sách câu hỏi
- `createdAt`: Thời gian tạo
- `updatedAt`: Thời gian cập nhật

### 1.5. StudentExam

**File**: `src/main/java/com/ptit/service/entity/StudentExam.java`
**Mục đích**: Đại diện cho bài làm của sinh viên
**Các trường chính**:

- `id`: ID của bài làm
- `studentId`: Mã sinh viên
- `exam`: Đề thi liên kết
- `status`: Trạng thái bài làm
- `score`: Điểm số
- `answers`: Danh sách câu trả lời

### 1.6. StudentAnswer

**File**: `src/main/java/com/ptit/service/entity/StudentAnswer.java`
**Mục đích**: Đại diện cho câu trả lời của sinh viên
**Các trường chính**:

- `id`: ID của câu trả lời
- `studentExam`: Bài làm liên kết
- `question`: Câu hỏi liên kết
- `answer`: Nội dung câu trả lời
- `score`: Điểm số

## 2. DTOs

### 2.1. QuestionDTO

**File**: `src/main/java/com/ptit/service/dto/QuestionDTO.java`
**Mục đích**: DTO cho việc tạo/cập nhật câu hỏi
**Các trường chính**:

- `type`: Loại câu hỏi
- `content`: Nội dung câu hỏi
- `options`: Danh sách đáp án (cho câu trắc nghiệm)
- `essayAnswer`: Đáp án (cho câu tự luận)

### 2.2. StudentExamDTO

**File**: `src/main/java/com/ptit/service/dto/StudentExamDTO.java`
**Mục đích**: DTO cho việc tạo bài làm
**Các trường chính**:

- `studentId`: Mã sinh viên
- `examId`: ID đề thi

## 3. Enums

### 3.1. QuestionType

**File**: `src/main/java/com/ptit/service/entity/QuestionType.java`
**Mục đích**: Enum định nghĩa loại câu hỏi
**Các giá trị**:

- `MULTIPLE_CHOICE`: Câu hỏi trắc nghiệm
- `ESSAY`: Câu hỏi tự luận

### 3.2. ExamStatus

**File**: `src/main/java/com/ptit/service/entity/ExamStatus.java`
**Mục đích**: Enum định nghĩa trạng thái bài làm
**Các giá trị**:

- `NOT_STARTED`: Chưa bắt đầu
- `IN_PROGRESS`: Đang làm
- `SUBMITTED`: Đã nộp

## 4. Validations

### 4.1. ValidQuestion

**File**: `src/main/java/com/ptit/service/dto/validation/ValidQuestion.java`
**Mục đích**: Annotation validation cho QuestionDTO
**Các ràng buộc**:

- Kiểm tra loại câu hỏi
- Kiểm tra nội dung câu hỏi
- Kiểm tra đáp án trắc nghiệm
- Kiểm tra đáp án tự luận

### 4.2. QuestionValidation

**File**: `src/main/java/com/ptit/service/dto/validation/QuestionValidation.java`
**Mục đích**: Class xử lý validation cho QuestionDTO
**Các phương thức**:

- `isValid()`: Kiểm tra tính hợp lệ của QuestionDTO
- `validateMultipleChoice()`: Kiểm tra câu hỏi trắc nghiệm
- `validateEssay()`: Kiểm tra câu hỏi tự luận

## 5. Configurations

### 5.1. SecurityConfig

**File**: `src/main/java/com/ptit/service/config/SecurityConfig.java`
**Mục đích**: Cấu hình bảo mật
**Các cấu hình**:

- Cấu hình CORS
- Cấu hình authentication
- Cấu hình authorization

### 5.2. SwaggerConfig

**File**: `src/main/java/com/ptit/service/config/SwaggerConfig.java`
**Mục đích**: Cấu hình Swagger UI
**Các cấu hình**:

- Cấu hình API documentation
- Cấu hình security schemes
- Cấu hình API info

## 1. Controllers

### 1.1. QuestionController

**File**: `src/main/java/com/ptit/service/controller/QuestionController.java`
**Mục đích**: Quản lý các thao tác liên quan đến câu hỏi
**APIs**:

- `GET /api/questions`: Lấy danh sách tất cả câu hỏi
- `GET /api/questions/{id}`: Lấy chi tiết một câu hỏi
- `GET /api/questions/type/{type}`: Lấy câu hỏi theo loại (trắc nghiệm/tự luận)
- `POST /api/questions/multiple-choice`: Tạo câu hỏi trắc nghiệm mới
- `POST /api/questions/essay`: Tạo câu hỏi tự luận mới
- `PUT /api/questions/{id}`: Cập nhật câu hỏi
- `DELETE /api/questions/{id}`: Xóa câu hỏi

### 1.2. ExamController

**File**: `src/main/java/com/ptit/service/controller/ExamController.java`
**Mục đích**: Quản lý các thao tác liên quan đến đề thi
**APIs**:

- `GET /api/exams`: Lấy danh sách tất cả đề thi
- `GET /api/exams/{id}`: Lấy chi tiết một đề thi
- `POST /api/exams`: Tạo đề thi mới
- `PUT /api/exams/{id}`: Cập nhật đề thi
- `DELETE /api/exams/{id}`: Xóa đề thi

### 1.3. StudentExamController

**File**: `src/main/java/com/ptit/service/controller/StudentExamController.java`
**Mục đích**: Quản lý các thao tác liên quan đến bài làm của sinh viên
**APIs**:

- `GET /api/student-exams`: Lấy danh sách tất cả bài làm
- `GET /api/student-exams/{id}`: Lấy chi tiết một bài làm
- `GET /api/student-exams/student/{studentId}`: Lấy bài làm theo mã sinh viên
- `GET /api/student-exams/exam/{examId}`: Lấy bài làm theo mã đề thi
- `GET /api/student-exams/status/{status}`: Lấy bài làm theo trạng thái
- `POST /api/student-exams`: Bắt đầu làm bài
- `POST /api/student-exams/{id}/submit`: Nộp bài
- `GET /api/student-exams/{id}/answers`: Lấy câu trả lời của bài làm
- `POST /api/student-exams/{id}/answers`: Lưu câu trả lời
- `POST /api/student-exams/answers/{answerId}/grade`: Chấm điểm câu tự luận

## 2. Services

### 2.1. QuestionService

**File**: `src/main/java/com/ptit/service/service/QuestionService.java`
**Mục đích**: Xử lý logic nghiệp vụ liên quan đến câu hỏi
**Các phương thức chính**:

- `findAll()`: Lấy tất cả câu hỏi
- `findById(Long id)`: Lấy câu hỏi theo ID
- `findByType(QuestionType type)`: Lấy câu hỏi theo loại
- `save(Question question)`: Lưu câu hỏi
- `delete(Long id)`: Xóa câu hỏi

### 2.2. ExamService

**File**: `src/main/java/com/ptit/service/service/ExamService.java`
**Mục đích**: Xử lý logic nghiệp vụ liên quan đến đề thi
**Các phương thức chính**:

- `findAll()`: Lấy tất cả đề thi
- `findById(Long id)`: Lấy đề thi theo ID
- `save(Exam exam)`: Lưu đề thi
- `delete(Long id)`: Xóa đề thi

### 2.3. StudentExamService

**File**: `src/main/java/com/ptit/service/service/StudentExamService.java`
**Mục đích**: Xử lý logic nghiệp vụ liên quan đến bài làm
**Các phương thức chính**:

- `findAll()`: Lấy tất cả bài làm
- `findById(Long id)`: Lấy bài làm theo ID
- `findByStudentId(String studentId)`: Lấy bài làm theo mã sinh viên
- `findByExamId(Long examId)`: Lấy bài làm theo mã đề thi
- `findByStatus(ExamStatus status)`: Lấy bài làm theo trạng thái
- `save(StudentExam studentExam)`: Lưu bài làm
- `findAnswersByStudentExamId(Long id)`: Lấy câu trả lời của bài làm
- `saveAnswer(StudentAnswer answer)`: Lưu câu trả lời

### 2.4. ExamGradingService

**File**: `src/main/java/com/ptit/service/service/ExamGradingService.java`
**Mục đích**: Xử lý logic chấm điểm bài thi
**Các phương thức chính**:

- `gradeMultipleChoiceAnswers(Long studentExamId)`: Chấm điểm phần trắc nghiệm
- `gradeEssayAnswer(Long answerId, double score)`: Chấm điểm câu tự luận

## 3. Repositories

### 3.1. QuestionRepository

**File**: `src/main/java/com/ptit/service/repository/QuestionRepository.java`
**Mục đích**: Truy vấn dữ liệu câu hỏi
**Các phương thức chính**:

- `findByType(QuestionType type)`: Tìm câu hỏi theo loại
- `findRandomQuestionsByType(QuestionType type, int limit)`: Lấy ngẫu nhiên câu hỏi theo loại

### 3.2. ExamRepository

**File**: `src/main/java/com/ptit/service/repository/ExamRepository.java`
**Mục đích**: Truy vấn dữ liệu đề thi
**Các phương thức chính**:

- `findByStatus(ExamStatus status)`: Tìm đề thi theo trạng thái

### 3.3. StudentExamRepository

**File**: `src/main/java/com/ptit/service/repository/StudentExamRepository.java`
**Mục đích**: Truy vấn dữ liệu bài làm
**Các phương thức chính**:

- `findByStudentId(String studentId)`: Tìm bài làm theo mã sinh viên
- `findByExamId(Long examId)`: Tìm bài làm theo mã đề thi
- `findByStatus(ExamStatus status)`: Tìm bài làm theo trạng thái
- `findByIdWithAnswers(Long id)`: Tìm bài làm kèm câu trả lời theo ID
- `findByStudentIdWithAnswers(String studentId)`: Tìm bài làm kèm câu trả lời theo mã sinh viên
- `findByExamIdWithAnswers(Long examId)`: Tìm bài làm kèm câu trả lời theo mã đề thi

## 4. Tests

### 4.1. Unit Tests

**File**: `src/test/java/com/ptit/service/service/StudentExamServiceTest.java`
**Mục đích**: Kiểm thử đơn vị cho StudentExamService
**Các test case**:

- `findAll_ShouldReturnAllStudentExams`
- `findByStudentId_ShouldReturnStudentExams`
- `findById_ShouldReturnStudentExam`
- `findById_ShouldThrowException_WhenNotFound`
- `findAnswersByStudentExamId_ShouldReturnAnswers`

### 4.2. Integration Tests

**File**: `src/test/java/com/ptit/service/controller/StudentExamControllerIntegrationTest.java`
**Mục đích**: Kiểm thử tích hợp cho StudentExamController
**Các test case**:

- `getAllStudentExams_ShouldReturnList`
- `getStudentExamById_ShouldReturnExam`
- `getStudentExamsByStudentId_ShouldReturnList`
- `startExam_ShouldCreateNewExam`
- `submitExam_ShouldGradeAnswers`
- `getStudentAnswers_ShouldReturnAnswers`

### 4.3. Performance Tests

**File**: `src/test/java/com/ptit/service/performance/StudentExamPerformanceTest.java`
**Mục đích**: Kiểm thử hiệu năng của các API
**Các test case**:

- `findAll_ShouldCompleteWithin500ms`
- `findByIdWithAnswers_ShouldCompleteWithin200ms`
- `findByStudentId_ShouldCompleteWithin300ms`
- `findByExamId_ShouldCompleteWithin300ms`
