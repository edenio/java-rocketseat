package br.com.rocketseat.todolist.PrimeiroController;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rota")
public class MeuPrimeiroController {

@GetMapping("/")
    public String primeMens() {

        return "Funcionou";
    }


    
}
