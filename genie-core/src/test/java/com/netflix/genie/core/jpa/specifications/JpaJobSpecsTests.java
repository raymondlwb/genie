/*
 * Copyright 2015 Netflix, Inc.
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */
package com.netflix.genie.core.jpa.specifications;

import com.netflix.genie.common.dto.JobStatus;
import com.netflix.genie.core.jpa.entities.ClusterEntity;
import com.netflix.genie.core.jpa.entities.CommandEntity;
import com.netflix.genie.core.jpa.entities.JobEntity;
import com.netflix.genie.core.jpa.entities.JobEntity_;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Test the specifications generated by JobSpecs.
 *
 * @author tgianos
 */
public class JpaJobSpecsTests {

    private static final String ID = UUID.randomUUID().toString();
    private static final String JOB_NAME = "jobName";
    private static final String USER_NAME = "tgianos";
    private static final String CLUSTER_NAME = "hprod2";
    private static final String CLUSTER_ID = "prod";
    private static final String COMMAND_NAME = "pig";
    private static final String COMMAND_ID = "pig14";
    private static final Set<String> TAGS = new HashSet<>();
    private static final Set<JobStatus> STATUSES = new HashSet<>();
    private static final String TAG = UUID.randomUUID().toString();

    private Root<JobEntity> root;
    private CriteriaQuery<?> cq;
    private CriteriaBuilder cb;
    private String tagLikeStatement;

    /**
     * Setup the mocks.
     */
    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        TAGS.clear();
        TAGS.add(TAG);

        STATUSES.clear();
        STATUSES.add(JobStatus.INIT);
        STATUSES.add(JobStatus.FAILED);

        this.root = (Root<JobEntity>) Mockito.mock(Root.class);
        this.cq = Mockito.mock(CriteriaQuery.class);
        this.cb = Mockito.mock(CriteriaBuilder.class);

        final Path<String> idPath = (Path<String>) Mockito.mock(Path.class);
        final Predicate likeIdPredicate = Mockito.mock(Predicate.class);
        Mockito.when(this.root.get(JobEntity_.id)).thenReturn(idPath);
        Mockito.when(this.cb.like(idPath, ID)).thenReturn(likeIdPredicate);

        final Path<String> jobNamePath = (Path<String>) Mockito.mock(Path.class);
        final Predicate likeJobNamePredicate = Mockito.mock(Predicate.class);
        Mockito.when(this.root.get(JobEntity_.name)).thenReturn(jobNamePath);
        Mockito.when(this.cb.like(jobNamePath, JOB_NAME))
                .thenReturn(likeJobNamePredicate);

        final Path<String> userNamePath = (Path<String>) Mockito.mock(Path.class);
        final Predicate equalUserNamePredicate = Mockito.mock(Predicate.class);
        Mockito.when(this.root.get(JobEntity_.user)).thenReturn(userNamePath);
        Mockito.when(this.cb.equal(userNamePath, USER_NAME))
                .thenReturn(equalUserNamePredicate);

        final Path<JobStatus> statusPath = (Path<JobStatus>) Mockito.mock(Path.class);
        final Predicate equalStatusPredicate = Mockito.mock(Predicate.class);
        Mockito.when(this.root.get(JobEntity_.status)).thenReturn(statusPath);
        Mockito.when(this.cb.equal(Mockito.eq(statusPath), Mockito.any(JobStatus.class)))
                .thenReturn(equalStatusPredicate);

        final Path<String> clusterNamePath = (Path<String>) Mockito.mock(Path.class);
        final Predicate equalClusterNamePredicate = Mockito.mock(Predicate.class);
        Mockito.when(this.root.get(JobEntity_.clusterName))
                .thenReturn(clusterNamePath);
        Mockito.when(this.cb.equal(clusterNamePath, CLUSTER_NAME))
                .thenReturn(equalClusterNamePredicate);

        final Path<ClusterEntity> clusterIdPath = (Path<ClusterEntity>) Mockito.mock(Path.class);
        final Predicate equalClusterIdPredicate = Mockito.mock(Predicate.class);
        Mockito.when(this.root.get(JobEntity_.cluster))
                .thenReturn(clusterIdPath);
        Mockito.when(this.cb.equal(clusterIdPath, CLUSTER_ID))
                .thenReturn(equalClusterIdPredicate);

        final Path<String> commandNamePath = (Path<String>) Mockito.mock(Path.class);
        final Predicate equalCommandNamePredicate = Mockito.mock(Predicate.class);
        Mockito.when(this.root.get(JobEntity_.commandName))
                .thenReturn(commandNamePath);
        Mockito.when(this.cb.equal(commandNamePath, COMMAND_NAME))
                .thenReturn(equalCommandNamePredicate);

        final Path<CommandEntity> commandIdPath = (Path<CommandEntity>) Mockito.mock(Path.class);
        final Predicate equalCommandIdPredicate = Mockito.mock(Predicate.class);
        Mockito.when(this.root.get(JobEntity_.command))
                .thenReturn(commandIdPath);
        Mockito.when(this.cb.equal(clusterIdPath, COMMAND_ID))
                .thenReturn(equalCommandIdPredicate);

        final Path<String> tagPath = (Path<String>) Mockito.mock(Path.class);
        final Predicate likeTagPredicate = Mockito.mock(Predicate.class);
        Mockito.when(this.root.get(JobEntity_.sortedTags)).thenReturn(tagPath);
        Mockito.when(this.cb.like(Mockito.eq(tagPath), Mockito.any(String.class)))
                .thenReturn(likeTagPredicate);

        this.tagLikeStatement = JpaSpecificationUtils.getTagLikeString(TAGS);
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindWithAll() {
        final Specification<JobEntity> spec = JpaJobSpecs.find(
                ID,
                JOB_NAME,
                USER_NAME,
                STATUSES,
                TAGS,
                CLUSTER_NAME,
                CLUSTER_ID,
                COMMAND_NAME,
                COMMAND_ID
        );

        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.id), ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.name), JOB_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.user), USER_NAME);
        for (final JobStatus status : STATUSES) {
            Mockito.verify(this.cb, Mockito.times(1))
                    .equal(this.root.get(JobEntity_.status), status);
        }
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.clusterName), CLUSTER_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.cluster), CLUSTER_ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.commandName), COMMAND_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.command), COMMAND_ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.sortedTags), this.tagLikeStatement);
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindWithOutId() {
        final Specification<JobEntity> spec = JpaJobSpecs.find(
                null,
                JOB_NAME,
                USER_NAME,
                STATUSES,
                TAGS,
                CLUSTER_NAME,
                CLUSTER_ID,
                COMMAND_NAME,
                COMMAND_ID
        );

        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.never())
                .like(this.root.get(JobEntity_.id), ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.name), JOB_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.user), USER_NAME);
        for (final JobStatus status : STATUSES) {
            Mockito.verify(this.cb, Mockito.times(1))
                    .equal(this.root.get(JobEntity_.status), status);
        }
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.clusterName), CLUSTER_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.cluster), CLUSTER_ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.commandName), COMMAND_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.command), COMMAND_ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.sortedTags), this.tagLikeStatement);
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindWithOutJobName() {
        final Specification<JobEntity> spec = JpaJobSpecs.find(
                ID,
                "",
                USER_NAME,
                STATUSES,
                TAGS,
                CLUSTER_NAME,
                CLUSTER_ID,
                COMMAND_NAME,
                COMMAND_ID
        );

        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.id), ID);
        Mockito.verify(this.cb, Mockito.never())
                .like(this.root.get(JobEntity_.name), JOB_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.user), USER_NAME);
        for (final JobStatus status : STATUSES) {
            Mockito.verify(this.cb, Mockito.times(1))
                    .equal(this.root.get(JobEntity_.status), status);
        }
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.clusterName), CLUSTER_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.cluster), CLUSTER_ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.commandName), COMMAND_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.command), COMMAND_ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.sortedTags), this.tagLikeStatement);
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindWithOutUserName() {
        final Specification<JobEntity> spec = JpaJobSpecs.find(
                ID,
                JOB_NAME,
                " ",
                STATUSES,
                TAGS,
                CLUSTER_NAME,
                CLUSTER_ID,
                COMMAND_NAME,
                COMMAND_ID
        );

        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.id), ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.name), JOB_NAME);
        Mockito.verify(this.cb, Mockito.never())
                .equal(this.root.get(JobEntity_.user), USER_NAME);
        for (final JobStatus status : STATUSES) {
            Mockito.verify(this.cb, Mockito.times(1))
                    .equal(this.root.get(JobEntity_.status), status);
        }
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.clusterName), CLUSTER_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.cluster), CLUSTER_ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.commandName), COMMAND_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.command), COMMAND_ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.sortedTags), this.tagLikeStatement);
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindWithOutStatus() {
        final Specification<JobEntity> spec = JpaJobSpecs.find(
                ID,
                JOB_NAME,
                USER_NAME,
                null,
                TAGS,
                CLUSTER_NAME,
                CLUSTER_ID,
                COMMAND_NAME,
                COMMAND_ID
        );

        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.id), ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.name), JOB_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.user), USER_NAME);
        for (final JobStatus status : STATUSES) {
            Mockito.verify(this.cb, Mockito.never())
                    .equal(this.root.get(JobEntity_.status), status);
        }
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.clusterName), CLUSTER_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.cluster), CLUSTER_ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.commandName), COMMAND_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.command), COMMAND_ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.sortedTags), this.tagLikeStatement);
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindWithEmptyStatus() {
        final Specification<JobEntity> spec = JpaJobSpecs.find(
                ID,
                JOB_NAME,
                USER_NAME,
                new HashSet<>(),
                TAGS,
                CLUSTER_NAME,
                CLUSTER_ID,
                COMMAND_NAME,
                COMMAND_ID
        );

        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.id), ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.name), JOB_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.user), USER_NAME);
        for (final JobStatus status : STATUSES) {
            Mockito.verify(this.cb, Mockito.never())
                    .equal(this.root.get(JobEntity_.status), status);
        }
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.clusterName), CLUSTER_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.cluster), CLUSTER_ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.commandName), COMMAND_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.command), COMMAND_ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.sortedTags), this.tagLikeStatement);
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindWithOutClusterName() {
        final Specification<JobEntity> spec = JpaJobSpecs.find(
                ID,
                JOB_NAME,
                USER_NAME,
                STATUSES,
                TAGS,
                null,
                CLUSTER_ID,
                COMMAND_NAME,
                COMMAND_ID
        );

        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.id), ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.name), JOB_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.user), USER_NAME);
        for (final JobStatus status : STATUSES) {
            Mockito.verify(this.cb, Mockito.times(1))
                    .equal(this.root.get(JobEntity_.status), status);
        }
        Mockito.verify(this.cb, Mockito.never())
                .equal(this.root.get(JobEntity_.clusterName), CLUSTER_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.cluster), CLUSTER_ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.commandName), COMMAND_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.command), COMMAND_ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.sortedTags), this.tagLikeStatement);
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindWithOutClusterId() {
        final Specification<JobEntity> spec = JpaJobSpecs.find(
                ID,
                JOB_NAME,
                USER_NAME,
                STATUSES,
                TAGS,
                CLUSTER_NAME,
                null,
                COMMAND_NAME,
                COMMAND_ID
        );

        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.id), ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.name), JOB_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.user), USER_NAME);
        for (final JobStatus status : STATUSES) {
            Mockito.verify(this.cb, Mockito.times(1))
                    .equal(this.root.get(JobEntity_.status), status);
        }
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.clusterName), CLUSTER_NAME);
        Mockito.verify(this.cb, Mockito.never())
                .equal(this.root.get(JobEntity_.cluster), CLUSTER_ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.commandName), COMMAND_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.command), COMMAND_ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.sortedTags), this.tagLikeStatement);
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindWithOutCommandName() {
        final Specification<JobEntity> spec = JpaJobSpecs.find(
                ID,
                JOB_NAME,
                USER_NAME,
                STATUSES,
                TAGS,
                CLUSTER_NAME,
                CLUSTER_ID,
                null,
                COMMAND_ID
        );

        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.id), ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.name), JOB_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.user), USER_NAME);
        for (final JobStatus status : STATUSES) {
            Mockito.verify(this.cb, Mockito.times(1))
                    .equal(this.root.get(JobEntity_.status), status);
        }
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.clusterName), CLUSTER_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.cluster), CLUSTER_ID);
        Mockito.verify(this.cb, Mockito.never())
                .equal(this.root.get(JobEntity_.commandName), COMMAND_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.command), COMMAND_ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.sortedTags), this.tagLikeStatement);
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindWithOutCommandId() {
        final Specification<JobEntity> spec = JpaJobSpecs.find(
                ID,
                JOB_NAME,
                USER_NAME,
                STATUSES,
                TAGS,
                CLUSTER_NAME,
                CLUSTER_ID,
                COMMAND_NAME,
                null
        );

        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.id), ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.name), JOB_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.user), USER_NAME);
        for (final JobStatus status : STATUSES) {
            Mockito.verify(this.cb, Mockito.times(1))
                    .equal(this.root.get(JobEntity_.status), status);
        }
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.clusterName), CLUSTER_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.cluster), CLUSTER_ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.commandName), COMMAND_NAME);
        Mockito.verify(this.cb, Mockito.never())
                .equal(this.root.get(JobEntity_.command), COMMAND_ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.sortedTags), this.tagLikeStatement);
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindWithOutTags() {
        final Specification<JobEntity> spec = JpaJobSpecs.find(
                ID,
                JOB_NAME,
                USER_NAME,
                STATUSES,
                null,
                CLUSTER_NAME,
                CLUSTER_ID,
                COMMAND_NAME,
                COMMAND_ID
        );

        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.id), ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.name), JOB_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.user), USER_NAME);
        for (final JobStatus status : STATUSES) {
            Mockito.verify(this.cb, Mockito.times(1))
                    .equal(this.root.get(JobEntity_.status), status);
        }
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.clusterName), CLUSTER_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.cluster), CLUSTER_ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.commandName), COMMAND_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.command), COMMAND_ID);
        Mockito.verify(this.cb, Mockito.never())
                .like(this.root.get(JobEntity_.sortedTags), this.tagLikeStatement);
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindWithEmptyTag() {
        TAGS.add("");
        final Specification<JobEntity> spec = JpaJobSpecs.find(
                ID,
                JOB_NAME,
                USER_NAME,
                STATUSES,
                TAGS,
                CLUSTER_NAME,
                CLUSTER_ID,
                COMMAND_NAME,
                COMMAND_ID
        );

        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.id), ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.name), JOB_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.user), USER_NAME);
        for (final JobStatus status : STATUSES) {
            Mockito.verify(this.cb, Mockito.times(1))
                    .equal(this.root.get(JobEntity_.status), status);
        }
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.clusterName), CLUSTER_NAME);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.cluster), CLUSTER_ID);
        Mockito.verify(this.cb, Mockito.times(1))
                .like(this.root.get(JobEntity_.sortedTags), this.tagLikeStatement);
    }

    /**
     * Test the find zombie specification.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testFindZombies() {
        final long diff = 54000L;
        final Date now = new Date();
        final Date before = new Date(now.getTime() - diff);

        final Path<Date> updatedPath = (Path<Date>) Mockito.mock(Path.class);
        final Predicate lessThanUpdatedPredicate = Mockito.mock(Predicate.class);
        Mockito.when(this.root.get(JobEntity_.updated)).thenReturn(updatedPath);
        Mockito.when(this.cb.lessThan(updatedPath, new Date(diff)))
                .thenReturn(lessThanUpdatedPredicate);

        final Path<JobStatus> runningPath = (Path<JobStatus>) Mockito.mock(Path.class);
        final Predicate equalRunningPredicate = Mockito.mock(Predicate.class);
        Mockito.when(this.root.get(JobEntity_.status)).thenReturn(runningPath);
        Mockito.when(this.cb.equal(runningPath, JobStatus.RUNNING))
                .thenReturn(equalRunningPredicate);

        final Path<JobStatus> initPath = (Path<JobStatus>) Mockito.mock(Path.class);
        final Predicate equalInitPredicate = Mockito.mock(Predicate.class);
        Mockito.when(this.root.get(JobEntity_.status)).thenReturn(initPath);
        Mockito.when(this.cb.equal(initPath, JobStatus.INIT))
                .thenReturn(equalInitPredicate);

        final Predicate orPredicate = Mockito.mock(Predicate.class);
        Mockito.when(this.cb.or(equalRunningPredicate, equalInitPredicate))
                .thenReturn(orPredicate);

        final Specification<JobEntity> findZombies = JpaJobSpecs.findZombies(
                now.getTime(),
                before.getTime()
        );
        findZombies.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1))
                .lessThan(this.root.get(JobEntity_.updated), new Date(diff));
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.status), JobStatus.RUNNING);
        Mockito.verify(this.cb, Mockito.times(1))
                .equal(this.root.get(JobEntity_.status), JobStatus.INIT);
        Mockito.verify(this.cb, Mockito.times(1))
                .or(Mockito.any(Predicate.class), Mockito.any(Predicate.class));
    }

    /**
     * Just for completing coverage.
     */
    @Test
    public void testProtectedConstructor() {
        Assert.assertNotNull(new JpaJobSpecs());
    }
}
