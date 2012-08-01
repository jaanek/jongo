/*
 * Copyright (C) 2011 Benoit GUEROUT <bguerout at gmail dot com> and Yves AMSELLEM <amsellem dot yves at gmail dot com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jongo;

import org.jongo.marshall.MarshallingException;
import org.jongo.model.Friend;
import org.jongo.util.ErrorObject;
import org.jongo.util.IdResultMapper;
import org.jongo.util.JongoTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.fail;
import static org.fest.assertions.Assertions.assertThat;

public class FindOneTest extends JongoTestCase {

    private MongoCollection collection;

    @Before
    public void setUp() throws Exception {
        collection = createEmptyCollection("users");
    }

    @After
    public void tearDown() throws Exception {
        dropCollection("users");
    }

    @Test
    public void canFindOne() throws Exception {
        /* given */
        collection.save(new Friend("John", "22 Wall Street Avenue"));

        /* when */
        Friend friend = collection.findOne("{name:'John'}").as(Friend.class);

        /* then */
        assertThat(friend.getName()).isEqualTo("John");
    }

    @Test
    public void canFindOneWithEmptyQuery() throws Exception {
        /* given */
        collection.save(new Friend("John", "22 Wall Street Avenue"));

        /* when */
        Friend friend = collection.findOne().as(Friend.class);

        /* then */
        assertThat(friend.getName()).isEqualTo("John");
    }

    @Test
    public void shouldFailWhenUnableToUnmarshallResult() throws Exception {
        /* given */
        collection.insert("{error: 'NotaDate'}");

        /* when */
        try {
            collection.findOne().as(ErrorObject.class);
            fail();
        } catch (MarshallingException e) {
            assertThat(e.getMessage()).contains(" \"error\" : \"NotaDate\"");
        }
    }

    @Test
    public void whenNoResultShouldReturnNull() throws Exception {
        assertThat(collection.findOne("{_id:'invalid-id'}").as(Object.class)).isNull();
        assertThat(collection.findOne("{_id:'invalid-id'}").map(new IdResultMapper())).isNull();
        assertThat(collection.find("{_id:'invalid-id'}").as(Object.class)).hasSize(0);
    }

}
