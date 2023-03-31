package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private User user;
    private User user2;

    @BeforeEach
    void beforeEach() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();

        user = new User(1L, "name", "email@gmail.com");
        user2 = new User(2L, "name2", "email2@gmail.com");
    }

    @Test
    void getAllUsers() throws Exception {
        Mockito
                .when(userService.getAllUsers())
                .thenReturn(Arrays.asList(user, user2));

        mvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    void getUserById() throws Exception {
        Mockito
                .when(userService.getUserById(Mockito.anyLong()))
                .thenReturn(user);

        mvc.perform(get("/users/{userId}", user.getId())
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));

    }

    @Test
    void addNewUser() throws Exception {
        Mockito
                .when(userService.addUser(Mockito.any()))
                .thenReturn(user);

        mvc.perform(post("/users")
                .content(mapper.writeValueAsString(user))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    void updateUser() throws Exception {
        Mockito
                .when(userService.updateUser(Mockito.any(), Mockito.anyLong()))
                .thenReturn(user);

        mvc.perform(patch("/users/{userId}", user.getId())
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    void deleteUser() throws Exception {
        mvc.perform(delete("/users/{userId}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(userService, Mockito.times(1))
                .deleteUser(user.getId());

    }
}