package br.com.rocketseat.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.rocketseat.todolist.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private UserRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var servletPath = request.getServletPath();

        if (servletPath.startsWith("/tasks/")) {

            // pegar autenticação(usuário e senha)
            var authorization = request.getHeader("Authorization");
            var user_password = authorization.substring("Basic".length()).trim();
            byte[] authDecode = Base64.getDecoder().decode(user_password);

            var authString = new String(authDecode);

            // "edenio","12345"
            String[] credentials = authString.split(":");
            String username = credentials[0];
            String password = credentials[1];

            System.out.println("Autorization");
            System.out.println(username);
            System.out.println(password);

            // Validar usuário
            var user = this.repository.findByUsername(username);
            if (user == null) {
                response.sendError(401);
            } else {
                var passwordVerifyer = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if (passwordVerifyer.verified) {
                    request.setAttribute("idUser", user.getId());
                    filterChain.doFilter(request, response);
                } else {
                    response.sendError(401);
                }
            }
            // Validar senha
            // Se tudo estiver ok segue viagem

        } else {
            filterChain.doFilter(request, response);
        }

    }

}
