package com.buttongames.butterflyserver.graphql.types;

import com.buttongames.butterflydao.hibernate.dao.impl.ButterflyUserDao;
import com.buttongames.butterflymodel.model.ButterflyUser;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This class holds all the top-level GraphQL queries that we want to expose to the API clients for server entities.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Component
public class ButterflyQuery {

    /**
     * DAO for interacting with <code>ButterflyUser</code> objects in the database.
     */
    private final ButterflyUserDao butterflyUserDao;

    @Autowired
    public ButterflyQuery(final ButterflyUserDao butterflyUserDao) {
        this.butterflyUserDao = butterflyUserDao;
    }

    /**
     * Find a Butterfly user given their ID.
     * @param id
     * @return
     */
    @GraphQLQuery(name = "findButterflyUserById")
    public ButterflyUser findButterflyUserById(@GraphQLArgument(name = "id") final long id) {
        return this.butterflyUserDao.findById(id);
    }

    /**
     * Find all Butterfly users.
     * @return
     */
    @GraphQLQuery(name = "findAllButterflyUsers")
    public List<ButterflyUser> findAllButterflyUsers() {
        return this.butterflyUserDao.findAll();
    }
}
