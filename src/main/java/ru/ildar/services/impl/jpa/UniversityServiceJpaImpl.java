package ru.ildar.services.impl.jpa;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import ru.ildar.database.entities.*;
import ru.ildar.database.repositories.UniDescriptionDAO;
import ru.ildar.database.repositories.UniversityDAO;
import ru.ildar.services.UniversityService;
import ru.ildar.services.factory.impl.JpaServiceFactory;

import java.util.ArrayList;
import java.util.List;

@Service
public class UniversityServiceJpaImpl implements UniversityService
{
    @Autowired
    private UniversityDAO universityDAO;
    @Autowired
    private UniDescriptionDAO uniDescriptionDAO;

    @Autowired
    private JpaServiceFactory serviceFactory;

    @Override
    public void addUniversity(University un)
    {
        universityDAO.save(un);
    }

    @Override
    public University getById(int id)
    {
        return universityDAO.findOne(id);
    }

    @Override
    public List<University> getUniversities(int pageNumber, int unisPerPage)
    {
        Slice<University> slice = universityDAO.findAll(new PageRequest(pageNumber, unisPerPage));
        List<University> result = new ArrayList<>();
        for(University un : slice)
            result.add(un);

        return result;
    }

    @Override
    public int getPagesCount(int unisPerPage)
    {
        return (int)Math.ceil((double)universityDAO.count() / unisPerPage);
    }

    @Override
    public University getByIdWithFaculties(int unId)
    {
        University un = getById(unId);
        Hibernate.initialize(un.getFaculties());
        return un;
    }

    @Override
    public Iterable<University> getUniversitiesByCity(int cityId)
    {
        return universityDAO.findByCity_Id(cityId);
    }

    @Override
    public void setCityAndAddUniversity(University university, int cityId)
    {
        University uni = universityDAO.findByCity_IdAndUnName(cityId, university.getUnName());
        if(uni != null)
            throw new DuplicateKeyException("University with such name and city already exists.");

        City city = serviceFactory.getCityService().getById(cityId);
        university.setCity(city);

        universityDAO.save(university);
    }

    @Override
    public void removeUniversity(int unId)
    {
        universityDAO.delete(unId);
    }

    @Override
    public void setImage(int unId, byte[] bytes)
    {
        University university = universityDAO.findOne(unId);
        university.setUnImage(bytes);
    }

    @Override
    public UniversityDescription getDescription(int unId, String lang)
    {
        return uniDescriptionDAO.findByUniversity_UnIdAndLanguage(unId, lang);
    }

    @Override
    public void setUniversityDescription(UniversityDescription descr, String authorName)
    {
        Person changeAuthor = serviceFactory.getPersonService().getByUserName(authorName);
        descr.setLastChangePerson(changeAuthor);
        uniDescriptionDAO.save(descr);
    }

    @Override
    public UniversityDescription getDescriptionByLanguageAbbrev(int unId, String langAbbrev)
    {
        Language lang = serviceFactory.getLanguageService().getLanguageByAbbreviation(langAbbrev);
        return uniDescriptionDAO.findByUniversity_UnIdAndLanguage(unId, lang.getLanguage());
    }

    @Override
    public UniversityDescription getFirstDescriptionForUniversity(int unId)
    {
        return uniDescriptionDAO.findOneByUniversity_UnId(unId);
    }

    @Override
    public void setCity(Integer unId, Integer cityId, String address)
    {
        University university = universityDAO.findOne(unId);
        university.setUnAddress(address);
        City city = serviceFactory.getCityService().getById(cityId);
        university.setCity(city);
    }
}
