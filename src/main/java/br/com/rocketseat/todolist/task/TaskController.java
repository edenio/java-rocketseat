package br.com.rocketseat.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.rocketseat.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository repository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        
        var idUser = request.getAttribute("idUser");
        taskModel.setIdUsers((UUID) idUser);

        //validar data e hora
        /* 
        var currentDate = LocalDateTime.now();
        if(currentDate.isAfter(taskModel.getStartAt()) 
        || currentDate.isAfter(taskModel.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de inicio e a data de termino deve ser maior que a data atual");
        }
        if(taskModel.getStartAt().isAfter(taskModel.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de inicio deve ser menor que a data de termino");
        }
        */

        var task = this.repository.save(taskModel);      
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request){
        var idUser = request.getAttribute("idUser");
        List<TaskModel> tasks = this.repository.findByIdUsers((UUID) idUser);
        return tasks;
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id){
        //var idUser = request.getAttribute("idUser");

        var task = this.repository.findById(id).orElse(taskModel);

        if(task == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Tarefa não encontrada");
        }

        var idUser = request.getAttribute("idUser");
        if(!task.getIdUsers().equals(idUser)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Usuário não tem permissão pra alterar essa tarefa");
        }

        Utils.copyNomNullProperties(taskModel, task);
        /* 
        taskModel.setIdUsers((UUID) idUser);
        taskModel.setId(id);
        */
        var taskUpdate = this.repository.save(task); 
        return ResponseEntity.ok().body(taskUpdate);



    }
    
}
