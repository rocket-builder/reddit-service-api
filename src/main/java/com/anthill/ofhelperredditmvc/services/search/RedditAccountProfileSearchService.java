package com.anthill.ofhelperredditmvc.services.search;


import com.anthill.ofhelperredditmvc.domain.RedditAccountProfile;
import com.anthill.ofhelperredditmvc.domain.RedditAccountProfileSearchFields;
import com.anthill.ofhelperredditmvc.domain.RedditAccountFilter;
import com.anthill.ofhelperredditmvc.domain.dto.RedditAccountProfileFilterDto;
import com.anthill.ofhelperredditmvc.exceptions.IncorrectInputDataException;
import com.anthill.ofhelperredditmvc.exceptions.UnknownSearchFieldException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static java.util.Objects.isNull;

@Service
public class RedditAccountProfileSearchService {

    public List<RedditAccountProfile> searchInProfiles(List<RedditAccountProfile> profiles, RedditAccountProfileFilterDto search) {
        return profiles.stream()
                .filter(profile -> {
                    if(nonNull(search.getBy()) && isNull(search.getFilters())) {

                        return searchBy(search.getBy(), search.getValue(), profile);
                    } else if(nonNull(search.getBy()) && nonNull(search.getFilters())){

                        return searchBy(search.getBy(), search.getValue(), profile) && filterBy(search.getFilters(), profile);
                    } else if(isNull(search.getBy()) && nonNull(search.getFilters())) {

                        return filterBy(search.getFilters(), profile);
                    } else
                        return isNull(search.getBy()) && isNull(search.getFilters());
                })
                .collect(Collectors.toList());
    }

    private boolean searchBy(RedditAccountProfileSearchFields field, String value, RedditAccountProfile profile) {
        switch (field) {
            case LOGIN: return profile.getRedditAccount().getLogin().contains(value);
            case PROXY: return profile.getProxy() != null && profile.getProxy().getFormattedValue().contains(value);
            default: throw new UnknownSearchFieldException();
        }
    }

    private boolean filterBy(RedditAccountFilter[] filtersArray, RedditAccountProfile profile){
        var filters = Arrays.asList(filtersArray);

        if(filters.contains(RedditAccountFilter.ALL) && filters.size() == 1){
            return true;
        }

        if((filters.contains(RedditAccountFilter.BANNED) && filters.contains(RedditAccountFilter.NOT_BANNED)) ||
           (filters.contains(RedditAccountFilter.SUSPEND) && filters.contains(RedditAccountFilter.NOT_SUSPEND))) {
            throw new IncorrectInputDataException("Incompatible filter fields passed");
        }

        var results = new ArrayList<Boolean>();
        for(var filter : filters) {
            switch (filter) {
                case BANNED: results.add(profile.getRedditAccount().isBanned()); break;
                case NOT_BANNED: results.add(!profile.getRedditAccount().isBanned()); break;
                case SUSPEND: results.add(profile.getRedditAccount().isSuspend()); break;
                case NOT_SUSPEND: results.add(!profile.getRedditAccount().isSuspend()); break;
            }
        }

        return results.stream().allMatch(result -> result);
    }
}
