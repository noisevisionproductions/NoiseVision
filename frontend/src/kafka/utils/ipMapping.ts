import api from "@/utils/axios";

interface GeoIpResponse {
    country: string;
    region: string;
    city: string;
    latitude: number;
    longitude: number;
}

export const getGeoIpInfo = async (ipAddress: string | null, fallBackText: {
    unknown: string;
    other: string;
    invalid: string;
}): Promise<GeoIpResponse | null> => {
    if (!ipAddress) return null;

    try {
        const {data} = await api.get<GeoIpResponse>(`/api/geoip/${ipAddress}`);
        return {
            country: data.country || fallBackText.unknown,
            city: data.city || fallBackText.unknown,
            latitude: data.latitude || 0,
            longitude: data.longitude || 0,
            region: data.region || fallBackText.unknown
        };
    } catch (error) {
        console.error('Error getting GeoIP info:', error);
        return null;
    }
};