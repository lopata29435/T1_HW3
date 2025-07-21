package command;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Command {
    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    private CommandPriority priority;
    
    @NotBlank(message = "Author is required")
    @Size(max = 100, message = "Author cannot exceed 100 characters")
    private String author;
    
    @NotBlank(message = "Time is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String time;

    //Вообще как будто бы хочетсся тут сразу присваивать уникальный id, но потом есть блок обработки уведомленинй который сам это обработает.
    // Мне показалось это более хорошо подходит под SRP
    private String id;
    //Это добавил чтобы была более понятная логика с командами
    private CommandStatus status = CommandStatus.PENDING;
    
    private Command() {}
    
    public Command(String description, CommandPriority priority, String author, String time) {
        this.description = description;
        this.priority = priority;
        this.author = author;
        this.time = time;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public CommandPriority getPriority() {
        return priority;
    }
    
    public void setPriority(CommandPriority priority) {
        this.priority = priority;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getTime() {
        return time;
    }
    
    public void setTime(String time) {
        this.time = time;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public CommandStatus getStatus() {
        return status;
    }
    
    public void setStatus(CommandStatus status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "Command{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", priority=" + priority +
                ", author='" + author + '\'' +
                ", time='" + time + '\'' +
                ", status=" + status +
                '}';
    }
}
