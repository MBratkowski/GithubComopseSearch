package io.bratexsoft.core.network.datasource

import io.bratexsoft.core.data.api.error.RepositoryNotFoundException
import io.bratexsoft.core.data.api.model.CommitsListItem
import io.bratexsoft.core.data.api.model.OrganizationInformation
import io.bratexsoft.core.data.api.model.Repositories
import io.bratexsoft.core.network.api.RepositoriesServiceApi
import io.bratexsoft.core.network.mapper.RepositoriesMapper
import io.bratexsoft.core.network.model.commits.toDomain
import io.bratexsoft.core.network.model.repositories.toDomain
import retrofit2.HttpException

class RepositoriesNetworkDataSourceImpl(
    private val repositoriesServiceApi: RepositoriesServiceApi,
    private val repositoriesMapper: RepositoriesMapper
) : RepositoriesNetworkDataSource {

    @SuppressWarnings("TooGenericExceptionCaught")
    override suspend fun getRepositories(organizationInformation: OrganizationInformation): Repositories {
        try {
            return repositoriesServiceApi.getRepositories(
                ownerName = organizationInformation.ownerName,
                repositoryName = organizationInformation.repositoryName
            ).let { repository -> repositoriesMapper.mapTo(repository) }
        } catch (e: Throwable) {
            when (e) {
                // I didn't have a time to make a correct error handling
                is HttpException -> {
                    throw RepositoryNotFoundException()
                }

                else -> throw e
            }
        }
    }

    override suspend fun getRepositoryCommits(organizationInformation: OrganizationInformation): List<CommitsListItem> {
        return repositoriesServiceApi.getRepositoryCommits(
            ownerName = organizationInformation.ownerName,
            repositoryName = organizationInformation.repositoryName
        ).map { it.toDomain() }
    }
}
