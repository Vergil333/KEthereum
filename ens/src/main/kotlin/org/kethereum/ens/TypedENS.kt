package org.kethereum.ens

import org.kethereum.eip137.ENSName
import org.kethereum.eip137.toNameHashByteArray
import org.kethereum.ens.generated.ENSRPCConnector
import org.kethereum.ens.generated.ResolverRPCConnector
import org.kethereum.model.Address
import org.kethereum.rpc.EthereumRPC
import org.komputing.kethereum.erc634.ERC634RPCConnector

class TypedENS(private val rpc: EthereumRPC,
               ensAddress: Address = ENS_DEFAULT_CONTRACT_ADDRESS) {

    private val ens = ENSRPCConnector(ensAddress, rpc)

    /**
     * get the resolver Address for this ENSName
     */
    fun getResolver(name: ENSName) = ens.resolver(name.toNameHashByteArray())

    /**
     * get an Ethereum Address associated with this ENSName
     */
    fun getAddress(name: ENSName): Address? {
        val bytes32 = name.toNameHashByteArray()
        val resolver = ens.resolver(bytes32)
        return if (resolver != null && resolver != ENS_ADDRESS_NOT_FOUND) {
            return ResolverRPCConnector(resolver, rpc).addr(bytes32)
        } else {
            null
        }
    }

    /**
     * get a text record as defined in ERC-634
     * https://github.com/ethereum/EIPs/blob/master/EIPS/eip-634.md
     */
    fun getTextRecord(name: ENSName, key: String): String? {
        val bytes32 = name.toNameHashByteArray()
        val resolver = ens.resolver(bytes32)
        return if (resolver != null && resolver != ENS_ADDRESS_NOT_FOUND) {
            return ERC634RPCConnector(resolver, rpc).text(bytes32, key)
        } else {
            null
        }
    }

    /**
     * get an e-mail address
     * from EIP-634 text record
     */
    fun getEmail(name: ENSName) = getTextRecord(name, "github")

    /**
     * get an URL
     * from EIP-634 text record
     */
    fun getURL(name: ENSName) = getTextRecord(name, "url")

    /**
     * get an avatar - a URL to an image used as an avatar or logo
     * from EIP-634 text record
     */
    fun getAvatar(name: ENSName) = getTextRecord(name, "avatar")

    /**
     * get a description
     * from EIP-634 text record
     */
    fun getDescription(name: ENSName) = getTextRecord(name, "description")


    /**
     * get a notice
     * from EIP-634 text record
     */
    fun getNotice(name: ENSName) = getTextRecord(name, "notice")

    /**
     * get a list of keywords - A list of keywords, ordered by most significant first; clients that interpresent this field may choose a threshold beyond which to ignore
     * from EIP-634 text record
     */
    fun getKeywords(name: ENSName) = getTextRecord(name, "keywords")?.let { it.split(",") }

    /**
     * get an github username
     * from EIP-634 text record
     */
    fun getGithubUserName(name: ENSName) = getTextRecord(name, "vnd.github")

    /**
     * get an twitter username
     * from EIP-634 text record
     */
    fun getTwitterUserName(name: ENSName) = getTextRecord(name, "vnd.twitter")
}