import { Sort } from "./Sort";

export interface Pageable{
    sort?: Sort;
    page: number;
    size: number;
    offset?: number;
    paged?: boolean;
    unpaged?: boolean;
}