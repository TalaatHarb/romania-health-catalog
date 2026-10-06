import { Sort } from "./Sort";

export interface Page<T> {
    content: T[];
    empty?: boolean;
    first?: boolean;
    last?: boolean;
    numberOfElements: number;
    pageable?: string;
    size: number;
    number: number;
    sort?: Sort;
    totalElements: number;
    totalPages: number;
}

export function emptyPage<T>(): Page<T>{
    return {
        content: [],
        size: 0,
        number: 0,
        totalElements: 0,
        totalPages: 0,
        numberOfElements: 0
    };
}