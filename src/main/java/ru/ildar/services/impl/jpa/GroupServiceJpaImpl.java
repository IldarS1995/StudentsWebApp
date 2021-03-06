package ru.ildar.services.impl.jpa;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import ru.ildar.database.entities.Faculty;
import ru.ildar.database.entities.Group;
import ru.ildar.database.repositories.GroupDAO;
import ru.ildar.services.GroupService;
import ru.ildar.services.factory.impl.JpaServiceFactory;

@Service
public class GroupServiceJpaImpl implements GroupService
{
    @Autowired
    private GroupDAO groupDAO;

    @Autowired
    private JpaServiceFactory serviceFactory;

    @Override
    public Group getGroupWithStudents(String groupId)
    {
        Group group = groupDAO.findOne(groupId);
        Hibernate.initialize(group.getStudents());
        return group;
    }

    @Override
    public Iterable<Group> getGroupsByFaculty(int facultyId)
    {
        return groupDAO.findByFaculty_FacultyIdOrderByGroupIdAscStudentsCountAsc(facultyId);
    }

    @Override
    public void addGroupToFaculty(Group group, int facultyId)
    {
        Group gr = groupDAO.findOne(group.getGroupId());
        if(gr != null)
            throw new DuplicateKeyException("Group with such ID already exists.");

        Faculty faculty = serviceFactory.getFacultyService().get(facultyId);
        group.setFaculty(faculty);
        groupDAO.save(group);
    }

    @Override
    public Group getGroupById(String groupId)
    {
        return groupDAO.findOne(groupId);
    }
}
