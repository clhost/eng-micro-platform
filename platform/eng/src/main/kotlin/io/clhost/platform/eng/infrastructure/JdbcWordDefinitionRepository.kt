package io.clhost.platform.eng.infrastructure

import io.clhost.platform.eng.domain.WordDefinition
import io.clhost.extension.jackson.jsonDecode
import io.clhost.extension.jackson.jsonEncode
import io.clhost.platform.eng.domain.WordDefinitionRepository
import java.sql.ResultSet
import java.time.OffsetDateTime
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component

@Component
class JdbcWordDefinitionRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) : WordDefinitionRepository {

    val wordDefinitionJdbcMapper = { rs: ResultSet, _: Int ->
        WordDefinition.reflect(
            word = rs.getString("word"),
            createdAt = rs.getObject("created_at", OffsetDateTime::class.java),
            updatedAt = rs.getObject("updated_at", OffsetDateTime::class.java),
            meanings = jsonDecode(rs.getString("meanings")),
            translations = jsonDecode(rs.getString("translations")),
            pronunciations = jsonDecode(rs.getString("pronunciations")),
            tags = jsonDecode(rs.getString("tags")),
            synonyms = jsonDecode(rs.getString("synonyms"))
        )
    }

    override fun get(word: String): WordDefinition? {
        val sql = """
            SELECT
                word,
                synonyms,
                translations,
                meanings,
                tags,
                pronunciations,
                created_at,
                updated_at
            FROM
                word_definition
            WHERE
                word = :word
        """.trimIndent()

        val params = MapSqlParameterSource()
            .addValue("word", word)

        return jdbcTemplate.query(sql, params, wordDefinitionJdbcMapper).firstOrNull()
    }

    override fun create(wordDefinition: WordDefinition) {
        val sql = """
            INSERT INTO word_definition (
                word,
                synonyms,
                translations,
                meanings,
                tags,
                pronunciations,
                created_at,
                updated_at
            ) VALUES (
                :word,
                :synonyms::JSONB,
                :translations::JSONB,
                :meanings::JSONB,
                :tags::JSONB,
                :pronunciations::JSONB,
                :created_at,
                :updated_at
            )
        """.trimIndent()

        val params = MapSqlParameterSource()
            .addValue("word", wordDefinition.word)
            .addValue("synonyms", jsonEncode(wordDefinition.synonyms))
            .addValue("translations", jsonEncode(wordDefinition.translations))
            .addValue("meanings", jsonEncode(wordDefinition.meanings))
            .addValue("tags", jsonEncode(wordDefinition.tags))
            .addValue("pronunciations", jsonEncode(wordDefinition.pronunciations))
            .addValue("created_at", wordDefinition.createdAt)
            .addValue("updated_at", wordDefinition.updatedAt)

        jdbcTemplate.update(sql, params)
    }

    override fun update(wordDefinition: WordDefinition) {
        val sql = """
            UPDATE word_definition
            SET
                synonyms = :synonyms::JSONB,
                translations = :translations::JSONB,
                meanings = :meanings::JSONB,
                tags = :tags::JSONB,
                pronunciations = :pronunciations::JSONB,
                updated_at = :updated_at
            WHERE
                word = :word
        """.trimIndent()

        val params = MapSqlParameterSource()
            .addValue("word", wordDefinition.word)
            .addValue("synonyms", jsonEncode(wordDefinition.synonyms))
            .addValue("translations", jsonEncode(wordDefinition.translations))
            .addValue("meanings", jsonEncode(wordDefinition.meanings))
            .addValue("tags", jsonEncode(wordDefinition.tags))
            .addValue("pronunciations", jsonEncode(wordDefinition.pronunciations))
            .addValue("updated_at", wordDefinition.updatedAt)

        jdbcTemplate.update(sql, params)
    }

    override fun delete(word: String) {
        val sql = """
            DELETE
            FROM
                word_definition
            WHERE
                word = :word
        """.trimIndent()

        val params = MapSqlParameterSource()
            .addValue("word", word)

        jdbcTemplate.update(sql, params)
    }
}
