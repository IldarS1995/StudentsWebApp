package ru.ildar.controllers.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import ru.ildar.controllers.pojos.StudentRegisterPojo;
import ru.ildar.database.entities.LocalizedCity;
import ru.ildar.database.entities.Person;
import ru.ildar.database.entities.Student;
import ru.ildar.services.CityService;
import ru.ildar.services.PersonService;
import ru.ildar.services.StudentService;
import ru.ildar.services.factory.ServiceFactory;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Student controller that handles registering new
 * students
 */
@Controller
@RequestMapping("/register/student")
public class StudentRegisterController
{
    private StudentService studentService;
    private PersonService personService;
    private CityService cityService;

    @Autowired
    private ServiceFactory serviceFactory;

    @PostConstruct
    private void construct()
    {
        cityService = serviceFactory.getCityService();
        personService = serviceFactory.getPersonService();
        studentService = serviceFactory.getStudentService();
    }

    @ModelAttribute("cities")
    public List<LocalizedCity> cities(Locale locale)
    {
        return cityService.getCitiesLocalizations(locale.getLanguage());
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView registerPage()
    {
        StudentRegisterPojo pojo = new StudentRegisterPojo();
        pojo.setRole("ROLE_STUDENT");
        return new ModelAndView("student/registration/studRegistration", "student", pojo);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ModelAndView registerPage(@ModelAttribute("student") @Valid StudentRegisterPojo stud,
                                     BindingResult result)
    {
        if(result.hasErrors())
        {
            return new ModelAndView("student/registration/studRegistration", "student", stud);
        }

        if(studentService.getByUsername(stud.getUsername()) != null)
            return regError("hasUsername", stud);

        stud.setPassword(new Md5PasswordEncoder().encodePassword(stud.getPassword(), null));
        Person person = new Person(stud.getUsername(), stud.getPassword(), stud.getRole());
        personService.addPerson(person);

        Student student = new Student();
        student.setUsername(stud.getUsername());
        studentService.setGroupAndAddStudent(student, stud.getGroupSelect());

        //authenticate user
        UserDetails authUser = new User(stud.getUsername(), stud.getPassword(),
                true, true, true, true, Arrays.asList(new SimpleGrantedAuthority(stud.getRole())));
        Authentication auth = new UsernamePasswordAuthenticationToken(authUser,
                null, authUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        return new ModelAndView("redirect:/info/student");
    }

    private ModelAndView regError(String attr, StudentRegisterPojo user)
    {
        ModelMap model = new ModelMap();
        model.addAttribute("user", user);
        model.addAttribute(attr, true);
        return new ModelAndView("student/registration/studRegistration", model);
    }
}
