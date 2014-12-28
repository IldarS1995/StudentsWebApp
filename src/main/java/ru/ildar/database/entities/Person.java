package ru.ildar.database.entities;

import javax.persistence.*;

/**
 * Created by Ildar on 28.12.14.
 */
@Entity
@Table(name = "PEOPLE", schema = "STUDENTS_APP", catalog = "")
public class Person
{
    private String username;
    private String password;
    private String role;
    private PersonDetails details;

    @Id
    @Column(name = "USERNAME")
    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    @Basic
    @Column(name = "PASSWORD")
    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    @Basic
    @Column(name = "ROLE_NAME")
    public String getRoleName()
    {
        return role;
    }

    public void setRoleName(String role)
    {
        this.role = role;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        return username.equals(person.getUsername());
    }

    @Override
    public int hashCode()
    {
        return username.hashCode();
    }

    @OneToOne(mappedBy = "person")
    public PersonDetails getDetails()
    {
        return details;
    }

    public void setDetails(PersonDetails details)
    {
        this.details = details;
    }
}