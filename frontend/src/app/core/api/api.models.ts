export interface TableDto {
  id: number;
  label: string;
  posX: number;
  posY: number;
  active: boolean;
  occupied: boolean;
}

export interface MenuItemDto {
  id: number;
  categoryId: number;
  name: string;
  price: number;
  active: boolean;
  imageUrl?: string | null;
}

export interface MenuCategoryDto {
  id: number;
  name: string;
  sortOrder: number;
  active: boolean;
  items: MenuItemDto[];
}

export interface OrderLineDto {
  id: number;
  menuItemId: number;
  menuItemName: string;
  quantity: number;
  unitPrice: number;
}

export interface OrderDto {
  id: number;
  tableId: number;
  shiftId: number;
  status: string;
  openedAt: string;
  closedAt: string | null;
  lines: OrderLineDto[];
}

export interface ShiftDto {
  id: number;
  waiterId: number;
  waiterFullName: string;
  status: string;
  startedAt: string;
  endedAt: string | null;
}